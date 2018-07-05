def ptog(s,p,num):#metis to vnm
    sg=[]
    f = open(s)
    info = f.readline()
    sg.append(info)
    alines = f.readlines()
    for line in alines:
        line=line.split()
        k=1
        kdict={}
        kdict['weight']=line[0]
        kdict['edge']={}
        while k<len(line):
            kdict['edge'][line[k]]=line[k+1]
            k=k+2;
        sg.append(kdict)
    
    f.close()
    
    f = open(p)
    pg=[]
    for i in range(0,num):
        kdict={}
        kdict['node']=[]
        kdict['weight']=0
        kdict['edge']={}
        pg.append(kdict)
    alines = f.readlines()
    i=1
    for line in alines:
        pg[int(line)]['node'].append(i)
        pg[int(line)]['weight']=pg[int(line)]['weight']+int(sg[i]['weight'])
        sg[i]['in']=int(line)
        i=i+1
    f.close()

    ct=0
    ect=0
    for pc in pg:
        for n in pc['node']:
            for k in sg[n]['edge']:
                if sg[int(k)]['in']!=ct:
                    if pg[ct]['edge'].has_key(sg[int(k)]['in']):
                        pg[ct]['edge'][sg[int(k)]['in']]=pg[ct]['edge'][sg[int(k)]['in']]+int(sg[n]['edge'][k])
                    else:
                        pg[ct]['edge'][sg[int(k)]['in']]=int(sg[n]['edge'][k])                     
        ct=ct+1
    print pg

    f = open(p+'.rs','w')
    f.write(str(num)+'\n')
    i=0
    while i<num:
        f.write(str(pg[i]['weight']))
        for key in pg[i]['edge']:
            f.write(' '+str(key+1)+' '+str(pg[i]['edge'][key]))
        f.write('\n')
        i=i+1   
def sumk(f1,f2):#计算K
    f = open(f1)#physical
    info = f.readline()
    alines = f.readlines()
    sum1=0
    ct=0
    for line in alines:
        ct=ct+1
        line=line.split()
        k=1
        t1=int(line[0])
        t2=0
        t3=0
        while k<len(line):
            t2=t2+int(line[k+1])
            t3=t3+1
            k=k+2;
        sum1=sum1+t1
    f.close()
    
    f = open(f2)#virtual
    info = f.readline()
    alines = f.readlines()
    sum2=0.0
    for line in alines:
        line=line.split()
        k=1
        t1=float(int(line[0]))
        t2=0.0
        t3=0
        while k<len(line):
            t2=t2+int(line[k+1])
            t3=t3+1
            k=k+2;
        t2=t2/100
        t1=t1/1000
        sum2=sum2+t1
    print t1
    print t2
    print sum1,ct,sum2
    sk=sum2/(sum1/(5*ct))#5 放大系数
    print sk
    f.close()
ptog('dns','dns-520',520)
#sumk('1000.s','dns')

