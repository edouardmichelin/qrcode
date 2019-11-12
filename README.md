# CS-107 - Mini projet 1 - QRCode

### Edouard Michelin & Julien Jordan

---

![alt text](https://chart.googleapis.com/chart?cht=qr&chl=I%20love%20java!&chs=180x180&choe=UTF-8&chld=L|2)

---

## Contexte

Ce projet a été donné dans le cadre du cours d'introduction à la programmation (CS-107).

## Objectif

L'bjectif premier de ce projet est d'être capable d'appliquer les premiers principes de programmation enseignés lors du cours dans le but de pouvoir recréer, en partie, l'ecriture d'un QR Code (V1 -> V4).

## Principes utilisés lors du mini projet

- Encodage de caractères en byte
- Manipulation des opérateurs bitwise
- Manipulation de tableaux multi dimensionnels
- Manipulation des bases enseignées lors des cours (conditional statements, loops, etc...)

---

## Partie 1

La partie 1 concerne l'encodage de l'input `String` en une chaine de bits, représentée par un tableau de booléens.

## Partie 2

La partie 2 concerne la création de la matrice (`int[][]`) de données.
Elle va de son initilisation à son remplissage de base, ce qui concerne les structures de base d'un QR Code (finder patterns, timing patterns, alignment patterns, dark module, the format informations)

## Partie 3

La partie 3 concerne le remplissage de notre matrice par les données encodées et formattées dans la partie 1.

## Partie bonus

Nous avons travaillé sur deux des bonus proposés dans le tutoriel.

#### Bonus 1 - Choix automatique du meilleur mask pour une version donnée

La méthode `int[][] renderQRCodeMatrix(int version, boolean[] data)` permet par polymorphisme de créer notre matrice sans spécifier de version de masque en paramètre.

Elle appellera premièrement une méthode pour trouver le meilleur masque pour les données fournies.

Ensuite appellera `int[][] renderQRCodeMatrix(int version, boolean[] data, int mask)` qui est son polymorphisme lui passant cette fois le meilleur masque trouvé comme paramètre afin de générer la matrice finale du QR code.


##### `int findBestMasking(int version, boolean[] data)`

Cette méthode permet de trouver le meilleur masque et reprend les deux paramètres passé à `renderQRCodeMatrix(int version, boolean[] data)` suivants:
- La version de la matrice
- Les données du message préalablement encodé et sous sa forme de booléens.

Elle va lancer une boucle qui va générer la matrice du QR code pour chacun des masques et déterminer quel est le meilleur masque selon un système de point de pénalité.

Les points de pénalités sont retournés par une méthode d'évaluation qui sera appelée sur chaque matrice pour chaque masque afin de les évaluer.

Un score en fonction de ses points de pénalité est gardé en mémoire et comparé avec le score de la dernière matrice générée, si le score est plus bas, on garde ce score ainsi que l'id du masque correspondant à la matrice de ce résultat.

Une fois le score le plus bas trouvé, la méthode retourne l'id du meilleur masque, soit celui qui a généré le score le plus bas.

##### `int evaluate(int[][] matrix)`

Ceci est la méthode d'évaluation appelée dans `int findBestMasking(int version, boolean[] data)` et prend comme paramètre
- La matrice finale du QR code

Cette matrice sera ensuite parcourue par une double boucle dans le but de rechercher des patterns pour ajouter des points de pénalités selon 4 règles :
- Sur chaque lignes et colonnes de la matrice, 5 modules de même couleurs consécutifs ajoutent 3 points de pénalités et 1 points de pénalité supplémentaire pour chaque module de même couleur après les 5 initiaux.
- Tous les carrés 2x2 de mêmes couleurs trouvés ajouteront 3 points de pénalité. Ces carrés peuvent se superposer. <i>Ex: un carré de 3x3 modules de même couleur compte 12 points de pénalités</i>.
- Sur chaque ligne et colonne deux séquences seront recherchées. La première étant `W, W, W, W, B, W, B, B, B, W, B` et la deuxième `B, W, B, B, B, W, B, W, W, W , W`. W = blanc et B = noir. Pour chaque séquence trouvée dans la matrice, un ajout de 40 points de pénalité est fait, le but étant de trouver un pattern similaire aux "finder patterns".
- Un calcul sera fait pour pénaliser un écart trop grand entre le nombre de modules blancs et le nombre de modules noirs. La formule appliquée fait le pourcentage de modules noirs qu'il y a dans la matrice, ensuite trouve le multiple de 5 précédent puis celui d'après (<i>Ex: 34% donnera 30 et 35, 35% donnera 35 et 40</i>). On soustrait ensuite 50 à ces deux valeurs et on retient la valeur absolue de chacune. La plus petite valeur entre les deux est ensuite multipliée par 2 et ajoutée aux points de pénalités.

Le total des points de pénalités est alors retourné. Un score bas indique une matrice bien diversifiée au niveau des modules, la rendant donc préférable car plus lisible par un scanner.

#### Bonus 2 - Affichage des <i>alignement patterns</i> pour une version donnée (≤ 40)

Un fichier `Extensions.java` a été ajouté dans le projet.

La classe `qrcode.Extensions` contient la méthode `int[] getAlignmentPatternsPositions(int version)` qui retourne un tableau d'entiers, chaque entier étant la position (qui sera à la fois sur l'axe des ordonnées et des abscisses) du module central d'un <i>alignment pattern</i>.

La classe `qrcode.MatrixConstruction` contient les méthodes suivantes

##### `void addAlignmentPattern(int[][] matrix, int topLeftCornerPosX, int topLeftCornerPosY)`

Cette méthode prend comme paramètres
- la matrice QR Code sur laquelle placer le alignment pattern
- la position X (col) du module en haut à gauche du pattern
- la position Y (row) du module en haut à gauche du pattern

##### `void addAlignmentPatterns(int[][] matrix, int version)`

Cette méthode prend comme paramètres
- la matrice sur laquelle placer les alignment patterns
- la version du QR Code

Elle récupère le tableau des positions des patterns (`getAlignmentPatternsPositions()`) et place un alignment pattern pour chaque coordonnée à la fois sur les axes X et Y (ex : `{ 6, 22 }` ==> `[6][6]`, `[22][22]`, `[6][22]`, `[22][6]`).

Attention : un alignment pattern n'est placé que s'il ne superpose pas de finder pattern.

Dans un soucis de temps, la méthode `getAlignmentPatternsPositions()` retourne des tableaux écrits en dur dans le code, une version 41 donnée en paramètre retournera donc un tableau par défaut. (Je n'aime pas écrire du code en dur mais bon...)

##### Comment tester cette partie du bonus ?

Etant donné que certaines méthodes provenant de `QRCodeInfos` génèrent une erreur lors de l'utilisation d'une version supérieure à 4, nous remplaçons la ligne `boolean[] encodedData = DataEncoding.byteModeEncoding(INPUT, VERSION);` par `boolean[] encodedData = new boolean[0];` dans `Main.java`.

Ceci implique que le QR Code ne sera pas lisible, les informations ajoutées dans la partie 1 étant manquantes.
