#!/usr/bin/env python
# This import registers the 3D projection, but is otherwise unused.
from mpl_toolkits.mplot3d import Axes3D  # noqa: F401 unused import
import os
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.ticker import LinearLocator, FormatStrFormatter
import numpy as np
import imageio
import cv2

path="/home/bomberman/Desktop/animes/temp/"
filenames=[]
if len(os.listdir(path) ) == 0:
	print("Creating images..." )
	file=open("bodysLog.txt")
	positions=[]
	velocity=[]
	size=int(file.readline())
	steps=int(file.readline())
	currentSep=0
	images=[]
	bodysX=[]
	bodysY=[]
	bodysZ=[]
	vB=[]
	printer=0
	before=0
	current=0
	for s in range(0,steps):
		current=(int((s/steps)*100))
		if( (current%10==0)and current!=before ):	
			print(str( int((s/steps)*100))+"%")
		positions.clear()
		for x in range(0,size):
			positions.append(float(file.readline()))
		for v in range(0,size):
			velocity.append(float(file.readline()))
		bodysX.clear()
		bodysY.clear()
		bodysZ.clear()
		vB.clear()
		counter=0
		for x in positions:
			if(counter==0):
				bodysX.append(x)
				counter+=1
			elif(counter==1):
				bodysY.append(x)
				counter+=1
			elif(counter==2):
				bodysZ.append(x)
				counter=0
		fig=plt.figure()
		counter=0
		vel=0
		for v in velocity:
			if (counter==0|counter==1):
				vel+=v
				counter+=1
			else:
				vel+=v
				counter=0
				vB.append(vel)
				vel=0
		ax=fig.gca(projection='3d')
		ax.set_xlabel('X-axis')
		ax.set_ylabel('Y-axis')
		ax.set_zlabel('Z-axis')
		ax.set_xlim3d(-4000,4000)
		ax.set_ylim3d(-4000,4000)
		ax.set_zlim3d(-4000,4000)
		ax.scatter3D(bodysX,bodysY,bodysZ,c='black')
		name=str(currentSep)+".png"
		plt.savefig(path+name)
		plt.close(fig)
		filenames.append(path+name)
		currentSep+=1
		before=int((s/steps)*100)
else:
	print("Images already in cache")	
path2="/home/bomberman/Desktop/animes/"
print("Making animation...")
files=[]
if(len(filenames)==0):
	for x in os.listdir(path):
		files.append(x)
aux=sorted(files,key=lambda x:int(x.split('.')[0]))
for f in aux:
	filenames.append(path+f)
size=len(filenames)
i=0
pathout='simulation.avi'
fps=60
frame_array=[]

for filename in filenames:
	img=cv2.imread(filename)
	height,widht,layers=img.shape
	sizeImage=(widht,height)
	frame_array.append(img)
out=cv2.VideoWriter(pathout,cv2.VideoWriter_fourcc(*'DIVX'), fps, sizeImage)
for i in range(size):
	out.write(frame_array[i])
out.release()
#with imageio.get_writer(path2+"anime.gif", mode='I',fps=20) as writer:
#    for filename in filenames:
#        image = imageio.imread(filename)
#        writer.append_data(image)
#        i+=1
#        print("Making anime "+str( (i/size)*100 )+"%" )

