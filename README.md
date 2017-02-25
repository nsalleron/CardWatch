#Projet imagerie de L3

Application mobile sous Android
	1°) Compatible Android 4.0+
	2°) OpenCV 2.4.10
3 modes d’utilisation:
	1°) Via Galerie
	2°) Via Groupe d’image
	3°) Via Camera du smartphone

# Algorithme de reconnaissance

	1°) Redimenssionnement de l'image
	2°) Lissage de l'image
	3°) Utilisation de l'algorithme kmean
	4°) Distinction premier plan, arrière plan.
	5°) Segmentation
	6°) Fermeture puis Ouverture de la Fermeture
	7°) Contours (Canny)
	8°) Recherche du plus grand contour avec utilisation de l'algorithme Suzuki85 et Approximation de contours
	9°) Récupération de 4 points pour la transformée en perspective
	10°) Calcul de la transformée en perspective pour 4 points.
	11°) Rognage suivant les 4 points et la matrice 3x3 obtenue précédemment
	12°) Binarisation inverse de l'image résultante 
	13°) Comparaison via différence absolue entre images
	14°) Le score le plus faible représente le peu de différence entre les deux images.
	15°) Reconnaissance de la classe, du nombre et de la couleur.

# Modes de reconnaissance

	Reconnaissance d'une image.
	Reconnaissance de groupe d'image avec taux de reconnaissance.
	Reconnaissance via Camera.

# Sources

	• Mastering OpenCV Android Application Programming
	• Android Application Programming with OpenCV
	• Speeding Up the Douglas-Peucker Line-Simplification Algorithm
		http://www.bowdoin.edu/~ltoma/teaching/cs350/spring06/Lecture-Handouts/hershberger92speeding.pdf
	• K-means clustering
		http://www.wikiwand.com/en/K-means_clustering
	• Canny Edge Detection
		http://www.cse.iitd.ernet.in/~pkalra/csl783/canny.pdf
	• Suzuki Algorithm ( Disponible dans Topological structural analysis of digitized binary images by border following)
	• Structural Analysis and Shape Descriptors
		http://docs.opencv.org/2.4/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html
	• Playing Card Using OpenCV & Python
		https://rdmilligan.wordpress.com/2014/07/05/playing-card-detection-using-opencv/
	• 24 automating games using opencv and Python
		http://arnab.org/blog/24-automating-card-games-using-opencv
	• OpenCV Documentation
		http://code.opencv.org/projects/opencv/repository

