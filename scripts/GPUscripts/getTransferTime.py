#!/usr/bin/python3
import re
timeframe=2 #(ms)
fl=open("out.txt","r")
text=fl.read()
subs=re.sub(r" ","",text)
lines=re.split(r"\n",subs)
fil=""
for x in lines:
	if "java/bin/java" in x:
		fil+=x+"\n"
fil2=""
fil2=re.sub(r".+java\/bin\/java","",fil)
fil3=""
fil3=re.sub(r"MiB\|","",fil2)
execTime=0
comTime=0
listofTime=[]
aux=""
for x in fil3:
	if x != "\n":
		aux+=x
	else:
		listofTime.append(int(aux))
		aux=""
listofCom=[]
listofExec=[]
timecounterCom=0
timecounterExec=0
ant=0
firstDecrease=0
dispCounter=0
dispList=[]
for actual in listofTime:
	#Enquanto o numero crescer está a transferir para a GPU
	if actual > ant:
		timecounterCom+=timeframe
		firstDecrease=0
		ant=actual
		dispList.append(dispCounter)
		dispCounter=0
	#Enquanto o numero mantiver-se está a executar
	elif actual == ant:
		timecounterExec+=timeframe
		ant=actual
	#Se o numero decrescer acabou a execução
	elif actual < ant and firstDecrease==0:
		listofExec.append(timecounterExec)
		timecounterExec=0
		ant=actual
		listofCom.append(timecounterCom)
		timecounterCom=0
		firstDecrease=1
	#Enquanto o numero decrescer, está a finalizar a execução, transferindo os dados necessários para memoria RAM e a fazer disposal do kernel
	elif actual < ant and firstDecrease==1:
		dispCounter+=1
		
	#Para debug
	else:
		print("Error, this message should not be appearing")

print("List of Com"+str(listofCom))
print("List of exec"+str(listofExec))
dif=listofCom[0]+listofExec[0]-listofCom[1]-listofExec[1]
print("Dif= "+str(dif))
print("List of disposal"+str(dispList))

