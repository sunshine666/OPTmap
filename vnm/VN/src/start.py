#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import os


if len(sys.argv) != 4:
	print "parameter is missed  here is " + str(len(sys.argv));#PGnumΪ����ͼ caseΪ1 ����������Ϊѭ������
else:
	PGnum = sys.argv[1]
	case = sys.argv[2]
	maxVnetNum = int(sys.argv[3])
	os.system("javac com/vnm/*.java")
	for i in range(1, maxVnetNum/10+1):
		cmd = "java -Xms1g -Xmx1g com.vnm.Controller " + PGnum + " " + case + " " + str(i * 1)
		os.system(cmd)	 
