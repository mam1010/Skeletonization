from scipy import ndimage
import cv2
'''
#First image
img1 = cv2.imread("C:\\Users\\mam1010\\Documents\\Research\\trial12Zhang-Thinner1.bmp",0)
dst1 = ndimage.distance_transform_edt(img1)
dim1 = dst1.shape
nodes1 = dim1[1] * dim1[0]

#Second image
#img2 = cv2.imread("C:\\Users\\mam1010\\Documents\\Research\\trial8conv7.bmp",0)
img2 = cv2.imread("C:\\Users\\mam1010\\Documents\\Research\\trial12Zhang-Thinner1.bmp",0)
dst2 = ndimage.distance_transform_edt(img2)
print(dst1[4][13])
dim2 = dst2.shape
nodes2 = dim2[1] * dim2[0]


sum1 = 0
sum2 = 0
#Summation 1
for j in range(0, dim1[0]):
    for k in range(0, dim1[1]):
        sum1 += (1/(((dst2[j][k])**2)+1))

for j in range(0, dim2[0]):
    for k in range(0, dim2[1]):
        sum2 += (1/(((dst1[j][k])**2)+1))

sum1 = sum1 / nodes1
sum2 = sum2 / nodes2

C = (sum1 + sum2)/2
print(C)
'''

img1 = cv2.imread("C:\\Users\\mam1010\\Documents\\Research\\trial12Zhang-Thinner1.bmp",0)
#img1 = cv2.imread("C:\\Users\\mam1010\\Documents\\Research\\temp.bmp",0)
dim1 = img1.shape
for x in range(0, dim1[0]):
    for y in range(0, dim1[1]):
        if img1[x][y]>0:
            img1[x][y] = 1
img2 = cv2.imread("C:\\Users\\mam1010\\Documents\\Research\\trial13conv7.bmp",0)
dim2 = img2.shape
for x in range(0, dim2[0]):
    for y in range(0, dim2[1]):
        if img2[x][y]>0:
            img2[x][y] = 1
K = 5
tempArr = [[0 for x in range(K)] for y in range(K)]
def dist(sel, x, y):
    val = ((K-1)/2)
    startx = x-val
    starty = y-val
    finishx = x+val
    finishy = y+val
    if(sel==1):
        tempArr = img1[startx:finishx,starty:finishy]
        tempArr1 = 1 - tempArr
    else:
        tempArr = img2[startx:finishx,starty:finishy]
        tempArr1 = 1 - tempArr
    tempArr2 = ndimage.distance_transform_edt(tempArr1)
    minni = tempArr2[K/2][K/2]
    return minni
#First image
nodes1 = 0
sum1 = 0
for x in range(K, dim1[0]-K):
    for y in range(K, dim1[1]-K):
        if img1[x][y]>0:
            nodes1 += 1
            d = dist(2, x, y)
            sum1 += (1/((d**2) + 1))
#Second image
nodes2 = 0
sum2 = 0
for x in range(K, dim1[0]-K):
    for y in range(K, dim1[1]-K):
        if img2[x][y]>0:
            nodes2 += 1
            d = dist(1, x, y)
            sum2 += (1/((d**2) + 1))
part1 = (sum1/nodes1)
part2 = (sum2/nodes2)
C = (part1+part2)/2
print(C)
