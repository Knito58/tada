import json, os, random
from PIL import Image
V='/home/user/w/assets/assets/minecraft/'
R='/home/user/w/tada/src/main/resources/'
A=R+'assets/tada/'; D=R+'data/tada/'
GOLD=[(255,214,64,255),(222,170,30,255),(255,236,140,255)]
AME=[(155,110,215,255),(190,150,245,255),(115,75,170,255)]
rnd=random.Random(58)
def w(path,obj):
    os.makedirs(os.path.dirname(path),exist_ok=True); open(path,'w').write(json.dumps(obj,indent=2))
def mask(im): return {(x,y) for y in range(im.height) for x in range(im.width) if im.getpixel((x,y))[3]>0}
def base_overlay(src, ov_src, big):
    im=Image.open(src).convert('RGBA'); m=mask(im)
    base=Image.new('RGBA',im.size,(0,0,0,0)); ov=Image.new('RGBA',im.size,(0,0,0,0))
    for (x,y) in m:
        l=sum(im.getpixel((x,y))[:3])/3
        v=int(max(185,min(255,222+ (l-128)*0.3 + rnd.randint(-14,14))))
        base.putpixel((x,y),(v,v,v,255))
    if big:
        for (x,y) in m:
            edge=any((x+dx,y+dy) not in m for dx,dy in((1,0),(-1,0),(0,1),(0,-1)))
            if edge and (x+y)%5==0: ov.putpixel((x,y),GOLD[(x*3+y)%3])
        for cx in range(3,im.width,16):
            for cy in range(3,im.height,16):
                cross=[(cx,cy),(cx+1,cy),(cx-1,cy),(cx,cy+1),(cx,cy-1)]
                if all(p in m for p in cross):
                    for i,p in enumerate(cross): ov.putpixel(p,AME[0 if i==0 else 1 if i in(1,3) else 2])
    else:
        o=Image.open(ov_src).convert('RGBA')
        for (x,y) in mask(o):
            if (x+y)%4==0: ov.putpixel((x,y),GOLD[(x+y)%3])
        xs=[p[0] for p in m]; ys=[p[1] for p in m]
        cx=(min(xs)+max(xs))//2; cy=(min(ys)+max(ys))//2+1
        for p,c in(((cx,cy),AME[1]),((cx+1,cy),AME[0]),((cx,cy+1),AME[2])):
            if p in m and ov.getpixel(p)[3]==0: ov.putpixel(p,c)
    return base,ov
# equipment textures
for layer in ['humanoid','humanoid_leggings','humanoid_baby']:
    b,o=base_overlay(V+f'textures/entity/equipment/{layer}/leather.png',V+f'textures/entity/equipment/{layer}/leather_overlay.png',True)
    for n,im in (('obsidian',b),('obsidian_overlay',o)):
        p=A+f'textures/entity/equipment/{layer}/{n}.png'; os.makedirs(os.path.dirname(p),exist_ok=True); im.save(p)
UNDYED=0xFF46424F-(1<<32)
w(A+'equipment/obsidian.json',{'layers':{l:[{'dyeable':{'color_when_undyed':UNDYED},'texture':'tada:obsidian'},{'texture':'tada:obsidian_overlay'}] for l in ['humanoid','humanoid_leggings','humanoid_baby']}})
MATS=['quartz','iron','netherite','redstone','copper','gold','emerald','diamond','lapis','amethyst','resin']
for piece in ['helmet','chestplate','leggings','boots']:
    b,o=base_overlay(V+f'textures/item/leather_{piece}.png',V+f'textures/item/leather_{piece}_overlay.png',False)
    for n,im in ((f'obsidian_{piece}',b),(f'obsidian_{piece}_overlay',o)):
        p=A+f'textures/item/{n}.png'; os.makedirs(os.path.dirname(p),exist_ok=True); im.save(p)
    name=f'obsidian_{piece}'
    w(A+f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':f'tada:item/{name}','layer1':f'tada:item/{name}_overlay'}})
    tint=[{'type':'minecraft:dye','default':UNDYED}]
    cases=[]
    for m in MATS:
        w(A+f'models/item/{name}_{m}_trim.json',{'parent':'minecraft:item/generated','textures':{'layer0':f'tada:item/{name}','layer1':f'tada:item/{name}_overlay','layer2':f'minecraft:trims/items/{piece}_trim_{m}'}})
        cases.append({'model':{'type':'minecraft:model','model':f'tada:item/{name}_{m}_trim','tints':tint},'when':f'minecraft:{m}'})
    w(A+f'items/{name}.json',{'model':{'type':'minecraft:select','property':'minecraft:trim_material','cases':cases,'fallback':{'type':'minecraft:model','model':f'tada:item/{name}','tints':tint}}})
    # dye recipe
    w(D+f'recipe/{name}_dyed.json',{'type':'minecraft:crafting_dye','dye':'#minecraft:dyes','group':'dyed_armor','target':f'tada:{name}','result':{'id':f'tada:{name}'}})
def shaped(name,pattern):
    w(D+f'recipe/{name}.json',{'type':'minecraft:crafting_shaped','category':'equipment','key':{'O':'minecraft:obsidian','A':'minecraft:amethyst_shard','G':'minecraft:gold_ingot'},'pattern':pattern,'result':{'id':f'tada:{name}'}})
shaped('obsidian_helmet',['OAO','G G'])
shaped('obsidian_chestplate',['O O','OAO','GOG'])
shaped('obsidian_leggings',['OAO','O O','G G'])
shaped('obsidian_boots',['O O','G A'])
w(D+'tags/item/repairs_obsidian_armor.json',{'values':['minecraft:obsidian']})
w(R+'data/minecraft/tags/item/trimmable_armor.json',{'values':[f'tada:obsidian_{p}' for p in ['helmet','chestplate','leggings','boots']]})
w(A+'lang/en_us.json',{f'item.tada.obsidian_{p}':f'Obsidian {p.capitalize()}' for p in ['helmet','chestplate','leggings','boots']})
