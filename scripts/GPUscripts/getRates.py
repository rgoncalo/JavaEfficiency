#!/usr/bin/python3
import re
fl=open("out.txt","r")
text=fl.read()
subs=re.sub(r" ","",text)
lines=re.split(r"\n",subs)
ret=""
for x in lines:
	if "Tx" in x:
		ret+="\n"+x
	elif "Rx" in x:
		ret+="\n"+x
toWrite=open("PCIE.out","w")
toWrite.write(ret)
toWrite.close()
fl.close()
fl2=open("PCIE.out","r")
text2=fl2.read()
lines2=re.split(r"\n",text2)
tcounter=0
rcounter=0
aux=""
start=0
Tx=[]
Rx=[]
for x in lines2:
	if "Tx" in x:
		for c in x:
			if c == ":":
				start=1
				continue
			if start==1 and c != "K":
				aux+=c
			if c == "K":
				start=0
				break
		if int(aux)!=0:
			tcounter+=1
			Tx.append(int(aux))
		aux=""
	if "Rx" in x:
		for c in x:
			if c ==":":
				start=1
				continue
			if start==1 and c != "K":
				aux+=c
			if c =="K":
				start=0
				break
		if int(aux)!=0:
			rcounter+=1
			Rx.append(int(aux))
		aux=""
totalTx=0
totalRx=0
for i in Tx:
	totalTx+=i
print("media Tx:"+str(totalTx/tcounter))
for i in Rx:
	totalRx+=i
print("media Rx:"+str(totalRx/rcounter))