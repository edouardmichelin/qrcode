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

Vas y coco c'est ton heure de gloire, écris proprement.

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
