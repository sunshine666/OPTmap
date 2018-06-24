import random
def btom(s,p,num):
    sg=[]
    f = open(s)
    info = f.readline()
    while info.find('Nodes:')<0 :
        info = f.readline()
    i=0
    alines = f.readlines()
    sg.append(num)
    k=0
    for line in alines:
        if i<num:
            bdict={}
            bdict['weight']=5#0.01+0.001*random.randint(0,1)
            bdict['edge']={}
            sg.append(bdict)
            i=i+1
            continue
        if i<num+3:
            i=i+1;
            continue
        line=line.split()
        sg[int(line[1])+1]['edge'][int(line[2])+1]=49+random.randint(0,2)#0.1+0.01*random.randint(0,1)
        sg[int(line[2])+1]['edge'][int(line[1])+1]=49+random.randint(0,2)#0.1+0.01*random.randint(0,1)
        k=k+1
    f.close()

    f = open(p,'w')
    i=1
    f.write(str(num)+' '+str(k)+' '+'011\n')
    while i<num+1 :
        f.write(str(int(1000*sg[i]['weight'])))
        for key in sg[i]['edge']:
            f.write(' '+str(key)+' '+str(int(100*sg[i]['edge'][key])))
        f.write('\n')
        i=i+1

    f = open(p+'.s','w')
    i=1
    f.write(str(num)+' '+str(k)+' '+'011\n')
    while i<num+1 :
        f.write(str(sg[i]['weight']))
        for key in sg[i]['edge']:
            f.write(' '+str(key)+' '+str(sg[i]['edge'][key]))
        f.write('\n')
        i=i+1
def dnstom(s,p):
    sg=[]
    addr={}
    f = open(s)
    alines = f.readlines()
    k=0
    tid=1
    i=0
    sg.append('dns')
    for line in alines:
        line=line.split()
        if line[0] not in addr:
            addr[line[0]]=tid
            tid=tid+1
        if line[1] not in addr:
            addr[line[1]]=tid
            tid=tid+1
    while i<len(addr)+1:
            bdict={}
            bdict['weight']=0.01+0.001*random.randint(0,1)
            bdict['edge']={}
            sg.append(bdict)
            i=i+1
    for line in alines:
        line=line.split()
        t=0.1+0.01*random.randint(0,1)
        sg[addr[line[0]]]['edge'][addr[line[1]]]=t
        sg[addr[line[1]]]['edge'][addr[line[0]]]=t
        k=k+1
    f.close()

    f = open(p,'w')
    i=1
    f.write(str(len(addr))+' '+str(k)+' '+'011\n')
    while i<len(addr)+1 :
        f.write(str(int(1000*sg[i]['weight'])))
        for key in sg[i]['edge']:
            f.write(' '+str(key)+' '+str(int(100*sg[i]['edge'][key])))
        f.write('\n')
        i=i+1

    f = open(p+'.s','w')
    i=1
    f.write(str(len(addr))+' '+str(k)+' '+'011\n')
    while i<len(addr)+1 :
        f.write(str(sg[i]['weight']))
        for key in sg[i]['edge']:
            f.write(' '+str(key)+' '+str(sg[i]['edge'][key]))
        f.write('\n')
        i=i+1

dnstom('data.dsv.txt','dns')
        
