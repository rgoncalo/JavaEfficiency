#!/usr/bin/env python
# This import registers the 3D projection, but is otherwise unused.
import os
from matplotlib import pyplot as plt
import matplotlib as mpl
import cv2
import imageio
path="/home/bomberman/Desktop/roomt/temp/"
filenames=[]
if len(os.listdir(path) ) == 0:
	print("Creating images..." )
	file=open("tempLog.txt")
	positions=[]
	velocity=[]
	size=int(file.readline())
	steps=int(file.readline())
	currentSep=0
	data=[]
	before=0
	v=0.0
	for s in range(0,steps):
		current=(int((s/steps)*100))
		if( (current%10==0)and current!=before ):	
			print(str( int((s/steps)*100))+"%")
		data=[]
		for x in range(0,size):
			sub_data=[]
			for y in range(0,size):
				v=(float(file.readline()))
				sub_data.append(v)
			data.append(sub_data)
			#print(data)
		plt.imshow(data,interpolation='nearest')
		#plt.show()
		name=str(currentSep)+".png"
		plt.savefig(path+name)
		plt.close()
		filenames.append(path+name)
		before=int((s/steps)*100)
		currentSep+=1
else:
	print("Images already in cache")	
path2="/home/bomberman/Desktop/roomt/"
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


##GIF
with imageio.get_writer(path2+"anime.gif", mode='I',fps=fps) as writer:
    for filename in filenames:
        image = imageio.imread(filename)
        writer.append_data(image)
        i+=1
        print("Making anime "+str( (i/size)*100 )+"%" )


##video
#for filename in filenames:
#	img=cv2.imread(filename)
#	height,widht,layers=img.shape
#	sizeImage=(widht,height)
#	frame_array.append(img)
#out=cv2.VideoWriter(pathout,cv2.VideoWriter_fourcc(*'DIVX'), fps, sizeImage)
#for i in range(size):
#	out.write(frame_array[i])
#out.release()