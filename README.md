# TestAccelerometreCompose
Basic Kotlin app that uses the Android Sensor Framework to manage the accelerometer when the user shakes the device. The UI is built with Jetpack Compose UI Toolkit

# MiniActv-1 - Bones Pràctiques en l'Ús de Sensors

## A) Exploració del Maneig de l'Acceleròmetre

### 1. **Bones pràctiques ja aplicades:**
- El registre i desregistre dels listeners de sensors es realitza correctament.
- El mètode `onSensorChanged()` realitza el mínim possible, evitant bloquejar l'execució.
- El retard del sensor es configura de manera adequada (`SENSOR_DELAY_NORMAL`).
- L'aplicació és compatible amb les restriccions d'Android 9+, ja que no cal utilitzar el servei en primer pla i el listener es desregistre adequadament.

### **Bones pràctiques que cal aplicar:**
- Comprovar l'existència del sensor abans d'utilitzar-lo.
- Afegir suport al `AndroidManifest.xml` per a dispositius sense el sensor.

---

### 2. **Comportament de l'App en diferents estats:**
- L'aplicació funciona correctament quan hi ha rotació de pantalla.
- No funciona en segon pla, el que era esperat, ja que el sensor es posa en pausa en aquest estat.

---

### 3. **Correccions aplicades:**
- La comprovació de l'existència del sensor ha estat implementada correctament.
- El suport al `AndroidManifest.xml` per a dispositius sense el sensor ha estat afegit correctament.

---

### 4. **Canvi d'Estructura de la Pantalla:**
- La pantalla es divideix en tres àrees:
  - **Zona superior**: on el color canviarà.
  - **Zona central**: on es mostrarà el missatge inicial "Shake to get a toast and to switch color", però només si el dispositiu té un acceleròmetre. En cas contrari, es mostrarà el missatge "Sorry, there is no accelerometer". També es proporcionarà la informació sobre les capacitats del sensor.
  - **Zona inferior**: utilitzada per mostrar informació rellevant, si és necessari.

---

## B) Incorporar el Maneig del Sensor de Llum

### 5. **Implementació del sensor de llum:**
- Tota la implementació del sensor de llum s'ha realitzat correctament, seguint les bones pràctiques.

### 6. **Àrea inferior per a la informació del sensor de llum:**
- L'àrea inferior s'ha configurat correctament, amb el fons groc i les informacions del sensor de llum mostrades segons sigui necessari.

### 7. **Definició de límits d'intensitat de llum:**
- Els límits d'intensitat de llum s'han definit correctament, amb els valors com es mostra en l'enunciat:
  - 1/3 del valor màxim: **LlindarLlumBaix**.
  - 2/3 del valor màxim: **LlindarLlumAlt**.

### 8. **Mostra dels missatges del sensor de llum:**
- Els missatges s'han implementat correctament, mostrant el valor del sensor i la intensitat (LOW, MEDIUM o HIGH).

### 9. **Filtrar les actualitzacions del sensor de llum:**
- El filtratge de les actualitzacions s'ha implementat correctament, basant-se en la diferència de valors i el temps.

### 10. **Ús d'àrea scrollable per als missatges:**
- L'àrea de missatges s'ha configurat com a "scrollable", permetent la visualització sense sobrecarregar la pantalla.

---

