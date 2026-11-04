#!/usr/bin/env python3
"""Upload the built jar to CurseForge via the author API.

Game versions are looked up by name, so nothing is hard-coded except the names.
Environment: CF_TOKEN, CF_PROJECT_ID, MOD_VERSION, RELEASE_TYPE, CHANGELOG.
"""
import glob
import json
import os
import subprocess
import sys
import urllib.request

API = "https://minecraft.curseforge.com/api"
HEADERS = {"X-Api-Token": os.environ["CF_TOKEN"]}
MC_VERSION = "26.3"
MC_VERSION_TYPE = "minecraft-26-3"   # slug of the version type that holds 26.3
WANTED = ["Fabric", "Client", "Server", "Java 25"]


def get(path):
    req = urllib.request.Request(f"{API}/{path}", headers=HEADERS)
    with urllib.request.urlopen(req, timeout=60) as resp:
        return json.load(resp)


types = {t["slug"]: t["id"] for t in get("game/version-types")}
versions = get("game/versions")
ids = []
mc = [v for v in versions if v["name"] == MC_VERSION and v["gameVersionTypeID"] == types[MC_VERSION_TYPE]]
if len(mc) != 1:
    sys.exit(f"expected exactly one Minecraft {MC_VERSION} entry, found {len(mc)}")
ids.append(mc[0]["id"])
for name in WANTED:
    found = [v for v in versions if v["name"] == name]
    if len(found) != 1:
        sys.exit(f"expected exactly one '{name}' entry, found {len(found)}")
    ids.append(found[0]["id"])

jars = [p for p in glob.glob("build/libs/*.jar") if not p.endswith(("-sources.jar", "-dev.jar", "-javadoc.jar"))]
if len(jars) != 1:
    sys.exit(f"expected exactly one jar in build/libs, found {jars}")

version = os.environ["MOD_VERSION"]
meta = {
    "changelog": os.environ["CHANGELOG"],
    "changelogType": "markdown",
    "displayName": f"TADA {version}",
    "gameVersions": ids,
    "releaseType": os.environ["RELEASE_TYPE"],
    "relations": {"projects": [
        {"slug": "fabric-api", "type": "requiredDependency"},
        {"slug": "modmenu", "type": "optionalDependency"},
    ]},
}
print("uploading", jars[0], "game version ids", ids)
# curl builds the multipart body; --fail-with-body makes HTTP errors fail the step
# and still prints the response body, so the CurseForge error message is visible.
subprocess.run(
    [
        "curl", "-sS", "--fail-with-body", "--max-time", "300",
        "-H", f"X-Api-Token: {os.environ['CF_TOKEN']}",
        "-F", f"metadata={json.dumps(meta)}",
        "-F", f"file=@{jars[0]}",
        f"{API}/projects/{os.environ['CF_PROJECT_ID']}/upload-file",
    ],
    check=True,
)
print()
