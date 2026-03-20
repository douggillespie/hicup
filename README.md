# hicup
PAMGuard plugins associated with Meygen 2022 HiCUP deployment as referenced in the paper

Gillespie D, Oswald M, Hastie G and Sparling C (2022) Marine Mammal HiCUP: A High Current Underwater Platform for the Long-Term Monitoring of Fine-Scale Marine Mammal Behavior Around Tidal Turbines. Front. Mar. Sci. 9:850446. [doi.org/10.3389/fmars.2022.850446](https://doi.org/10.3389/fmars.2022.850446)

**ArduinoPlugin** Handles UDP comms with an Arduino in the HiCUP junction box

**EtecPreamplifier** Used the Arduino to switch relays that control gain and filter settings on an Etec preamplifier

**Sonar Orientation** Controls actuators on the HiCUP to orientate the sonars, measures tilt/roll, activates battery charger, UV LED's, Camera, etc. 

**CompleteArduinoCode** Arduino code that builds for an Arduino Mega 2560 with a big Screw shield breakout board, and an Ethernet board. 

![Image of HiCUP junction box](/images/jbox1.jpg)

![Image of HiCUP junction box](/images/jbox2.jpg)


