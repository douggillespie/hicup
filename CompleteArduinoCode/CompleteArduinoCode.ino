/*
 ActuatorControl:

 created 19 Sep 2018
 by Michael Oswald
 */


#include <Ethernet.h>
#include <EthernetUdp.h>
#include <EEPROM.h>

// **************************************************************************
// define the debug flag.  Wrap debugging print statements with #ifdef / #endif to show.
// comment out this line to elimiate the Serial.print stuff and save program memory
#define progDEBUG

// other definitions here
#define UDP_TX_PACKET_MAX_SIZE 80 // override the default of 24 characters
#define MAXNUMCMDELEMENTS 5 // no more than 5 elements in a user command

// **************************************************************************
// Ethernet parameters
// Enter a MAC address and IP address for your controller below.
// Using the values from the original installation, PanTiltBNO055
byte mac[] = { 0x90, 0xA2, 0xDA, 0x10, 0x26, 0x82 };
IPAddress ip(192, 168, 0, 207);
IPAddress dns(192, 168, 0, 1);
IPAddress gateway(192, 168, 0, 1);
IPAddress subnet(255, 255, 255, 0);
unsigned int localPort = 8888;      // local port to listen on
EthernetUDP Udp;
char packetBuffer[UDP_TX_PACKET_MAX_SIZE];  // buffer to hold incoming packet,
char replyOK[] = "ACK"; // a positive response - command received and understood
char replyNotOK[] = "NAK"; // a negative response - command not understood
char reply[60]; // the actual reply to send back


// **************************************************************************
// General constants/variables
const int SDcardSPI = 4; // use pin 4 is used for the SD card SPI bus
const int etherSPI = 10; // use pin 10 is used for the ethernet SPI bus


// **************************************************************************
// Constants/Variables for Linear Actuators
const int actPos[] = {2,3};  // use digital pin 2/3 for actuator 0/1 position (dig pin used in PWM mode)
const int actPwr[] = {22,27}; // use digital pin 22/27 for actuator 0/1 power
const int actRst[] = {23,28}; // use digital pin 23/28 for resetting actuator 0/1
const int actMov[] = {24,29}; // use digital pin 24/29 to indicate whether actuator 0/1 is moving (LOW) or not (HIGH)
const int actFault[] = {25,30}; // use digital pin 25/30 for monitoring fault from actuator 0/1
const int actHallV = 26;  // use digital pin 26 to select between actuator 0 & 1 hall sensor voltage
int actStatus[2]; // monitor the status of actuators - moving or stationary
int actPosMem[] = {0,0};  // save the actuator positions in memory

// **************************************************************************
// Constants/Variables for Pan/Tilt/Roll board (FSM300 in UART-RVC mode)
const int orientPwr[] = {35,36}; // use digital pin 35/36 FSM300 board 0/1 power
const int orientRst[] = {37,38}; // use digital pin 37/38 FSM300 board 0/1 reset
int cargoIdx = 0;  // used for sonar orientation board readings
uint8_t cargo[120];   // used for sonar orientation board readings
float yaw,pitch,roll; // used for sonar; quaternions q0 = qw 1 = i; 2 = j; 3 = k;
int cargoIdx2 = 0;  // used for frame orientation board readings
uint8_t cargo2[120];   // used for frame orientation board readings
float yaw2,pitch2,roll2; // used for frame; quaternions q0 = qw 1 = i; 2 = j; 3 = k;

// **************************************************************************
// Constants/Variables for Etec amplifier board
const int nEtecPins = 3;
const int etecGain[] = {43, 44, 45}; // digital pins for etec amplifier gain control
const int etecFilter[] = {46, 47, 48}; // digital pins for etec amplifier filter control. 

// **************************************************************************
// Constants/Variables for Sonar Units
// NOT USED - SONAR UNITS WILL ALWAYS BE ON
// const int sonarPwr[] = {32,33}; // use digital pin 32/33 for sonar 0/1 power

// **************************************************************************
// Constants/Variables for Camera
const int camPwr = 34; // use digital pin 34 for camera power

// **************************************************************************
// Constants/Variables for UVC Lights
const int uvPwr[] = {40,41}; // use digital pin 40/41 for UV light 0/1 power
boolean uvOn[] = {false,false};
long uvTimer[] = {0L,0L};
int dutyCycle[] = {20,20};   // duty cycle in minutes

// **************************************************************************
// Constants/Variables for Battery Charger
const int battCharger = 49; // use digital pin 49 for battery charger

// **************************************************************************
// Constants/Variables for Spare relay position (#8)
const int sparePwr = 39; // use digital pin 39 for spare relay



// **************************************************************************
// ******************************   Setup   *********************************
// **************************************************************************
void setup() {
  // disable the SD card so that there's no conflicts with the ethernet
  pinMode(SDcardSPI,OUTPUT);
  digitalWrite(SDcardSPI,HIGH);

  // if we want to use the SD card, comment out the above
  // lines and uncomment the following lines. This temporarily disables ethernet
  // card SPI bus while we're initializing the SD card
  //digitalWrite(etherSPI,HIGH);
  //SD.begin(SDcardSPI);
  
  // start the Ethernet.  This has to be before all of the serial ports are initialized - I
  // don't know why, but if it comes later the Arduino just gets stuck looping through
  // the setup() method
  Ethernet.begin(mac, ip);

  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  while (!Serial);

  // start UDP
  Udp.begin(localPort);

  // Check for Ethernet hardware present
  if (Ethernet.hardwareStatus() == EthernetNoHardware) {
    Serial.println("Ethernet shield was not found.  Sorry, can't run without hardware. :(");
    while (true) {
      delay(1); // do nothing, no point running without Ethernet hardware
    }
  }
  
  if (Ethernet.linkStatus() == LinkOFF) {
    Serial.println("Ethernet cable is not connected.");
  }

  // Open serial communications with Sonar FSM300 orientation board
  // Note that we need to use the internal pullup resistor in order to
  // make sure the input isn't floating.  Serial2 (connected to the
  // junction box tilt/roll) would randomly lose the signal because the
  // input was floating
  Serial1.begin(115200);  // UART-RVC mode
  pinMode( 19, INPUT_PULLUP ); // fix Serial1

  // Open serial communications with Frame FSM300 orientation board
  // Note that we need to use the internal pullup resistor in order to
  // make sure the input isn't floating.  Serial2 (connected to the
  // junction box tilt/roll) would randomly lose the signal because the
  // input was floating
  // 2020-10-21 change to Serial3, because we're still losing comm sometimes
  Serial3.begin(115200);  // UART-RVC mode
  pinMode( 15, INPUT_PULLUP ); // fix Serial3


  // Intialize pins for the actuator control boards
  // HIGH turns a pin on (5V), LOW turns a pin off (0V)
  //
  // The relay board has an inverse input, so a HIGH pin actually
  // turns the relay off and a LOW pin turns it on.
  for (int i = 0; i < 2; i++) {
    pinMode(actPwr[i],OUTPUT);
    digitalWrite(actPwr[i],HIGH); // make sure the board is off (wired through normally open relay)
    pinMode(actMov[i],INPUT);
    pinMode(actFault[i],INPUT);
    pinMode(actRst[i],OUTPUT);
    digitalWrite(actRst[i],LOW); // only reset when we get the command
  }
  pinMode(actHallV,OUTPUT);  // act 0 (tilt) wired through NC relay, act 1 (roll) through NO relay
  digitalWrite(actHallV,HIGH);  // set board to use act 0 (tilt) hall voltage (relay off)

  // intialize pins for the FSM300 pan/tilt/roll boards
  // HIGH turns a pin on (5V), LOW turns a pin off (0V)
  //
  // The relay board has an inverse input, so a HIGH pin actually
  // turns the relay off and a LOW pin turns it on.
  pinMode(orientPwr[0],OUTPUT);
  digitalWrite(orientPwr[0],HIGH); // make sure the board is off (wired through normally open relay)
  pinMode(orientPwr[1],OUTPUT);
  digitalWrite(orientPwr[1],HIGH); // make sure the board is off (wired through normally open relay)
  cargoIdx=0;
  cargoIdx2=0;

  // initialise pins for the gain and filter control
  // HIGH turns a pin on (5V), LOW turns a pin off (0V)
  for (int i = 0; i < nEtecPins; i++) {
    pinMode(etecGain[i], OUTPUT);
    digitalWrite(etecGain[i], HIGH);
  }  
  for (int i = 0; i < nEtecPins; i++) {
    pinMode(etecFilter[i], OUTPUT);
    digitalWrite(etecFilter[i], HIGH);
  }

  // initialize pins for sonar heads
  // HIGH turns a pin on (5V), LOW turns a pin off (0V)
  //
  // The relay board has an inverse input, so a HIGH pin actually
  // turns the relay off and a LOW pin turns it on.
//  pinMode(sonarPwr[0],OUTPUT);
//  digitalWrite(sonarPwr[0],HIGH); // make sure the sonar is on (b/c sonar will be connected Normally Closed)
//  pinMode(sonarPwr[1],OUTPUT);
//  digitalWrite(sonarPwr[1],HIGH); // make sure the sonar is on (b/c sonar will be connected Normally Closed)

  // initialize pins for camera
  pinMode(camPwr,OUTPUT);
  digitalWrite(camPwr,HIGH); // make sure the camera is off (wired through normally open relay)

  // initialize pins for UVC lights
  // HIGH turns a pin on (5V), LOW turns a pin off (0V)
  //
  // The relay board has an inverse input, so a HIGH pin actually
  // turns the relay off and a LOW pin turns it on.
  pinMode(uvPwr[0],OUTPUT);
  digitalWrite(uvPwr[0],HIGH); // make sure the light is off (wired through normally open relay)
  pinMode(uvPwr[1],OUTPUT);
  digitalWrite(uvPwr[1],HIGH); // make sure the light is off (wired through normally open relay)

  // initialize pins for battery charger
  // HIGH turns a pin on (5V), LOW turns a pin off (0V)
  //
  // The relay board has an inverse input, so a HIGH pin actually
  // turns the relay off and a LOW pin turns it on.
  pinMode(battCharger,OUTPUT);
  digitalWrite(battCharger,HIGH); // make sure the battery charger is off (wired through normally open relay)
  
  // initialize pins for spare relay#8
  pinMode(sparePwr,OUTPUT);
  digitalWrite(sparePwr,HIGH); // make sure the spare is off (wired through normally open relay)

  // read the current position values from the EEPROM, and set the analogwrite
  actPosMem[1] = actPosMem[0] + sizeof(int);
  analogWrite(actPos[0],EEPROM.read(actPosMem[0]));      
  analogWrite(actPos[1],EEPROM.read(actPosMem[1]));      

/*
 * Remove the interrupts - just causing problems with the UDP transmission.  See
 * the ISR comments below for more information
  attachInterrupt(digitalPinToInterrupt(act1mov), act1StatusCheck, CHANGE);
  attachInterrupt(digitalPinToInterrupt(act1fault), act1FaultDetected, HIGH);
*/
  Serial.println("Ready to go");
}


// **************************************************************************
// ***************************   Main Loop   ********************************
// **************************************************************************
void loop() {
  
  // if there's data available, read a packet
  int packetSize = Udp.parsePacket();
  if (packetSize) {

    #ifdef progDEBUG
      Serial.print("\n\nReceived packet of size ");
      Serial.println(packetSize);
      Serial.print("From ");
      IPAddress remote = Udp.remoteIP();
      for (int i=0; i < 4; i++) {
        Serial.print(remote[i], DEC);
        if (i < 3) {
          Serial.print(".");
        }
      }
      Serial.print(", port ");
      Serial.println(Udp.remotePort());
    #endif

    // read the packet into packetBufffer
    Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    #ifdef progDEBUG
      Serial.println("Contents:");
      Serial.println(packetBuffer);
    #endif

    // Parse the string to determine the command
    char* token;
    String usrCmd[MAXNUMCMDELEMENTS];
    usrCmd[0]=String(packetBuffer);
    int i=0;
    token = strtok(packetBuffer,",");
    while (token != NULL) {
      usrCmd[i]=String(token);
      usrCmd[i].trim();
      i++;
      token=strtok(NULL,",");
    }
    int totalCommands = i;
    #ifdef progDEBUG
      for (int i=0; i<MAXNUMCMDELEMENTS; i++) {
        Serial.print("usrCmd[");
        Serial.print(i);
        Serial.print("]=***");
        Serial.print(usrCmd[i]);
        Serial.println("***");
      }
    #endif

    // set the default response to ok
    strncpy(reply,replyOK, sizeof(reply)-1);


    // ***********************************************************************
    // List of recognized commands - use if..else structure instead of
    // switch..case structure because it's faster and smaller (see
    // https://forum.arduino.cc/index.php?topic=44519.0)
    // ***********************************************************************

    
    // ***********************************************************************
    // Command Ping: check communication - just returns replyOK
    if (usrCmd[0]=="PING") {
        // do nothing extra here, just send back the ACK
        sprintf(reply,"PING,%s",replyOK);
      }
    
    // *******************************************************************
    // Command ACTPWR,x,ON/OFF/CHECK : to turn Actuator Control Board on/off
    // where x = actuator number (0 or 1).
    // Before turning either linear actuator on, make sure the battery charger
    // is off
    //
    // Valid arguments ON/OFF/CHECK
    //
    // Note: because we need to share the Hall Voltage signal between the two
    // boards, we need to make sure only one is turned on at a time.  So if
    // we turn one on, we turn the other off first.  Next time, we need a cable
    // with enough wires for two Hall Voltage signals and then we don't need to
    // worry about this.
    else if (usrCmd[0]=="ACTPWR") {
      int act = usrCmd[1].toInt();
      if (usrCmd[2]=="ON") {
        if (act==0) { // if we want to turn on actuator 0 (tilt)
          digitalWrite(battCharger,HIGH); // turn the battery charger off
          sprintf(reply,"BATTCHARGER,0,OFF,%s",replyOK);
          digitalWrite(actPwr[1],HIGH); // turn act 1 off
          sprintf(reply,"ACTPWR,1,OFF,%s",replyOK);
          digitalWrite(actPwr[0],LOW); // turn act 0 on
          sprintf(reply,"ACTPWR,0,ON,%s",replyOK);
          digitalWrite(actHallV,HIGH);  // set hall voltage to act 0
        }
        else {  // if we want to turn on actuator 1 (roll)
          digitalWrite(battCharger,HIGH); // turn the battery charger off
          sprintf(reply,"BATTCHARGER,0,OFF,%s",replyOK);
          digitalWrite(actPwr[0],HIGH); // turn act 0 off
          sprintf(reply,"ACTPWR,0,OFF,%s",replyOK);
          digitalWrite(actPwr[1],LOW); // turn act 1 on
          sprintf(reply,"ACTPWR,1,ON,%s",replyOK);
          digitalWrite(actHallV,LOW);  // set hall voltage to act 1
        }
      }
      else if (usrCmd[2]=="OFF") {
        digitalWrite(actPwr[act],HIGH); // turn the board off
        sprintf(reply,"ACTPWR,%d,OFF,%s",act,replyOK);
      }
      else {
        int stat = digitalRead(actPwr[act]);
        sprintf(reply,"ACTPWRCHK,%d,%d,%s",act,!stat,replyOK);
      }
    }
    
    // *******************************************************************
    // Command ACTPOS,x,y : to set the position of Actuator
    // where x=actuator number (0/1), y=position (0-255)
    else if (usrCmd[0]=="ACTPOS") {
      int act = usrCmd[1].toInt();
      int newPos = usrCmd[2].toInt();
      if (newPos > 255) newPos=255;
      if (newPos < 0) newPos=0;
      #ifdef progDEBUG
        Serial.print("The new position is ");
        Serial.println(newPos);
      #endif

      // map position 0-100 to pwm range 0-255
      // newPos = map(newPos,0,100,0,255);
      analogWrite(actPos[act],newPos);
      EEPROM.write(actPosMem[act],newPos);      
     
      sprintf(reply,"ACTPOS,%d,%d,%s",act,newPos,replyOK);
   }

    // *******************************************************************
    // Command ACTRST,x : to reset Actuator control board
    // where x = actuator number (0 or 1)
    else if (usrCmd[0]=="ACTRST") {
      int act = usrCmd[1].toInt();
      #ifdef progDEBUG
        Serial.println("Resetting Actuator Control Board");
      #endif
      digitalWrite(actRst[act],HIGH);
      delay(2000);
      digitalWrite(actRst[act],LOW);
      sprintf(reply,"ACTRST,%d,%s",act,replyOK);
    }

    // *******************************************************************
    // Command ORIENTPWR,x,ON/OFF/CHECK : to turn FSM300 Board on/off or check status
    // where x = board number (0 or 1)
    // Valid arguments ON/OFF/CHECK
    else if (usrCmd[0]=="ORIENTPWR") {
      int brd = usrCmd[1].toInt();
      if (usrCmd[2]=="ON") {
        digitalWrite(orientPwr[brd],LOW); // turn the board on (LOW b/c board is normally open)
        sprintf(reply,"ORIENTPWR,%d,ON,%s",brd,replyOK);
      }
      else if (usrCmd[2]=="OFF") {
        digitalWrite(orientPwr[brd],HIGH); // turn the board off
        sprintf(reply,"ORIENTPWR,%d,OFF,%s",brd,replyOK);
      }
      else {
        int stat = digitalRead(orientPwr[brd]);
        sprintf(reply,"ORIENTPWRCHK,%d,%d,%s",brd,!stat,replyOK);
      }
    }
    
    // *******************************************************************
    // Command GETORIENT,x : to get the current orientation
    // where x = orientation board number (0 or 1)
    else if (usrCmd[0]=="GETORIENT") {
      int board = usrCmd[1].toInt();
      if (board==0) {
        #ifdef progDEBUG
          Serial.println("Getting current sonar orientation");
        #endif
        boolean success = getSonarOrient();
        if (!success) {
          sprintf(reply,"GETORIENT,%d,999,999,999,%s",board,replyNotOK);
        }
        else {
          #ifdef progDEBUG
              Serial.print ("; Yaw "); Serial.print (yaw + 0.05f,1);
              Serial.print ("; Pitch "); Serial.print (pitch + 0.05f,1);                   // including rounding
              Serial.print ("; Roll "); Serial.println (roll + 0.05f,1);
          #endif
          char yawStr [7];
          char pitchStr [7];
          char rollStr [7];
          dtostrf(yaw, 6, 1, yawStr);       // convert to string, because Arduino sprintf doesn't recognize floats
          dtostrf(pitch, 6, 1, pitchStr);   // convert to string, because Arduino sprintf doesn't recognize floats
          dtostrf(roll, 6, 1, rollStr);     // convert to string, because Arduino sprintf doesn't recognize floats
          sprintf(reply,"GETORIENT,%d,%s,%s,%s,%s",board,yawStr,pitchStr,rollStr,replyOK);
        }
      }
      else {
        #ifdef progDEBUG
          Serial.println("Getting current frame orientation");
        #endif
        boolean success = getFrameOrient();
        if (!success) {
          sprintf(reply,"GETORIENT,%d,999,999,999,%s",board,replyNotOK);
        }
        else {
          #ifdef progDEBUG
              Serial.print ("; Yaw "); Serial.print (yaw2 + 0.05f,1);
              Serial.print ("; Pitch "); Serial.print (pitch2 + 0.05f,1);                   // including rounding
              Serial.print ("; Roll "); Serial.println (roll2 + 0.05f,1);
          #endif
          char yaw2Str [7];
          char pitch2Str [7];
          char roll2Str [7];
          dtostrf(yaw2, 6, 1, yaw2Str);       // convert to string, because Arduino sprintf doesn't recognize floats
          dtostrf(pitch2, 6, 1, pitch2Str);   // convert to string, because Arduino sprintf doesn't recognize floats
          dtostrf(roll2, 6, 1, roll2Str);     // convert to string, because Arduino sprintf doesn't recognize floats
          sprintf(reply,"GETORIENT,%d,%s,%s,%s,%s",board,yaw2Str,pitch2Str,roll2Str,replyOK);
        }
      }
     }
    
    // *******************************************************************
    // Command ETECGAIN,x : to set the preamplifier gains
    // where x = bitmap of gain values
    else if (usrCmd[0]=="ETECGAIN") {
      if (totalCommands < 2) {
        //strcpy(reply, "Unable to find pin data for ETECGAIN");
        sprintf(reply, "ETECGAIN,0,%s", replyNotOK); // add in a deviceID of 0, since all replies need an ID
      }
      else {
        int pins = atoi(usrCmd[1].c_str());
        setEtecPins(etecGain, pins, nEtecPins);
        sprintf(reply, "ETECGAIN,0,%d,%s", pins,replyOK);
      }
    }
    
    // *******************************************************************
    // Command ETECFILTER,x,y : to set the preamplifier filters
    // where x = orientation board number (0 or 1)
    else if (usrCmd[0]=="ETECFILTER") {
      if (totalCommands < 2) {
        //strcpy(reply, "Unable to find pin data for ETECFILTER");
        sprintf(reply, "ETECFILTER,0,%s", replyNotOK);
      }
      else {
        int pins = atoi(usrCmd[1].c_str());
        setEtecPins(etecFilter, pins, nEtecPins);
        sprintf(reply, "ETECFILTER,0,%d, %s", pins,replyOK);
      }
    }
    
    // *******************************************************************
    // Command SONARPWR,x,ON/OFF/CHECK : to turn sonar unit on/off
    // where x = sonar number (0 or 1)
    // Valid arguments ON/OFF/CHECK
//    else if (usrCmd[0]=="SONARPWR") {
//      int act = usrCmd[1].toInt();
//      if (usrCmd[2]=="ON") {
//        digitalWrite(sonarPwr[act],HIGH); // turn the board on (HIGH b/c power is normally closed)
//        sprintf(reply,"SONARPWR,%d,ON,%s",act,replyOK);
//      }
//      else if (usrCmd[2]=="OFF") {
//        digitalWrite(sonarPwr[act],LOW); // turn the board off (LOW b/c power is normally closed)
//        sprintf(reply,"SONARPWR,%d,OFF,%s",act,replyOK);
//      }
//      else {
//        int stat = digitalRead(sonarPwr[act]);
//        sprintf(reply,"SONARPWRCHK,%d,%d,%s",act,stat,replyOK);
//      }
//    }
    
    // Command CAMERA,ON/OFF/CHECK : to turn camera on/off
    //
    // Valid arguments ON/OFF/CHECK
    else if (usrCmd[0]=="CAMERA") {
      if (usrCmd[2]=="ON") {
        digitalWrite(camPwr,LOW); // turn the camera on (LOW b/c board is normally open)
        sprintf(reply,"CAMERA,0,ON,%s",replyOK);
      }
      else if (usrCmd[2]=="OFF") {
        digitalWrite(camPwr,HIGH); // turn the camera off
        sprintf(reply,"CAMERA,0,OFF,%s",replyOK);
      }
      else {
        // return whether the battery charger is on
        int stat = digitalRead(camPwr);
        sprintf(reply,"CAMERACHK,0,%d,%s",!stat,replyOK);
      }
    }

    
    
    // *******************************************************************
    // Command UVPWR,x,ON/OFF/CHECK : to turn UVC lights on/off or check status
    // where x = light number (0=Sonar or 1=PAM)
    // Valid arguments ON/OFF/CHECK
    else if (usrCmd[0]=="UVPWR") {
      int brd = usrCmd[1].toInt();
      if (usrCmd[2]=="ON") {
        uvOn[brd] = true;
        uvTimer[brd] = millis();
        digitalWrite(uvPwr[brd],LOW); // turn the board on (LOW b/c board is normally open)
        sprintf(reply,"UVPWR,%d,ON,%s",brd,replyOK);
      }
      else if (usrCmd[2]=="OFF") {
        uvOn[brd] = false;
        digitalWrite(uvPwr[brd],HIGH); // turn the board off
        sprintf(reply,"UVPWR,%d,OFF,%s",brd,replyOK);
      }
      else {
        // return whether the board is on, and what the duty cycle is set to
        int stat = digitalRead(uvPwr[brd]);
        sprintf(reply,"UVPWRCHK,%d,%d,%d,%s",brd,!stat,dutyCycle[brd],replyOK);
      }
    }
    
    // *******************************************************************
    // Command UVDUTY,x,y : to set the duty cycle of the UVC light
    // where x=light (0=sonar/1=PAM), y=time in minutes
    else if (usrCmd[0]=="UVDUTY") {
      int light = usrCmd[1].toInt();
      dutyCycle[light] = usrCmd[2].toInt();
      sprintf(reply,"UVDUTY,%d,%d,%s",light,dutyCycle[light],replyOK);
    }

    
    // *******************************************************************
    // Command BATTCHARGER,ON/OFF : to turn battery charger on/off
    // Before turning the battery charger on, make sure that the linear
    // actuators are both off
    //
    // Valid arguments ON/OFF
    else if (usrCmd[0]=="BATTCHARGER") {
      if (usrCmd[2]=="ON") {
        digitalWrite(actPwr[0],HIGH); // turn act 0 off
        sprintf(reply,"ACTPWR,0,OFF,%s",replyOK);
        digitalWrite(actPwr[1],HIGH); // turn act 1 off
        sprintf(reply,"ACTPWR,1,OFF,%s",replyOK);
        digitalWrite(battCharger,LOW); // turn the board on (LOW b/c board is normally open)
        sprintf(reply,"BATTCHARGER,0,ON,%s",replyOK);
      }
      else if (usrCmd[2]=="OFF") {
        digitalWrite(battCharger,HIGH); // turn the board off
        sprintf(reply,"BATTCHARGER,0,OFF,%s",replyOK);
      }
      else {
        // return whether the battery charger is on
        int stat = digitalRead(battCharger);
        sprintf(reply,"BATTCHARGERCHK,0,%d,%s",!stat,replyOK);
      }
    }
    
    
    // Command SPARE,ON/OFF/CHECK : to turn spare relay on/off
    // currently hooked up to Relay#8
    //
    // Valid arguments ON/OFF/CHECK
    else if (usrCmd[0]=="SPARE") {
      if (usrCmd[2]=="ON") {
        digitalWrite(sparePwr,LOW); // turn the spare on (LOW b/c board is normally open)
        sprintf(reply,"SPARE,0,ON,%s",replyOK);
      }
      else if (usrCmd[2]=="OFF") {
        digitalWrite(sparePwr,HIGH); // turn the spare off
        sprintf(reply,"SPARE,0,OFF,%s",replyOK);
      }
      else {
        // return whether the spare is on
        int stat = digitalRead(sparePwr);
        sprintf(reply,"SPARECHK,0,%d,%s",!stat,replyOK);
      }
    }



   // *******************************************************************
    // Command not recognized
    else {
      strncpy(reply,replyNotOK, sizeof(reply)-1);
      #ifdef progDEBUG
        Serial.println("Sorry - Command not recognized");
      #endif
    }

    // *******************************************************************
    // send a reply to the IP address and port that sent us the packet we received
    sendUDPMessage(reply);
  }

  // clear the packetBuffer array
  memset(packetBuffer,0,sizeof(packetBuffer));

  // do a quick check if actuator status changes from moving to stopped, or vice-versa
  // if stat=LOW (0), actuator is moving
  // if stat=HIGH (1), actuator is stopped
  for (int i=0; i<2; i++) {
    int stat = digitalRead(actMov[i]);
    if (stat != actStatus[i]) {
      sprintf(reply,"ACTMOV,%d,%d,%s",i,stat,replyOK);
      sendUDPMessage(reply);
      actStatus[i]=stat;
    }
  }

  // toggle the UV lights if they are on and enough time has elapsed
  for (int i=0; i<2; i++) {
    if (uvOn[i] && (millis()-uvTimer[i] >= dutyCycle[i] * 60 * 1000L)) {
      digitalWrite(uvPwr[i],!digitalRead(uvPwr[i]));
      uvTimer[i]=millis();
    }
  }
 
  // do a quick check if either actuator has thrown a fault
  /* DON'T DO THIS - THE C2-20 CONTROLLER BOARDS SEEM TO THROW
   *  CONTINUOUS FAULT MESSAGES THAT END UP FLOODING THE COMMS
   *  
  for (int i=0; i<2; i++) {
    int stat = digitalRead(actFault[i]);
    if (stat == HIGH) {
      char response[20];
      sprintf(response,"%s%d%s","Actuator ",i+1," fault");
      sendUDPMessage(response);
    }
  }
  */
}

/*
 * Send a UDP message to the current UDP client
 */
void sendUDPMessage(char response[]) {
  #ifdef progDEBUG
    Serial.print("Response= ");
    Serial.println(response);
  #endif
  Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
  int retVal = Udp.write(response);
  Udp.endPacket();  
}

/*
 * Get data from the Sonar FSM300 Pan/Tilt/Roll board
 */
boolean getSonarOrient(){                                                               
  #ifdef progDEBUG
    if (Serial1.available()){
      Serial.println("Serial1 is ready ");
    }
    else {
      Serial.println("********** Serial1 is NOT ready ");
      return false;
    }
  #endif
  while (Serial1.available()&& cargoIdx<100){
    cargo[cargoIdx] = Serial1.read();
    cargoIdx++;
  }

  // check if we've captured enough data.  A message from the board is 19 bytes long, but
  // there's no telling if we've captured it right at the beginning or if it's halfway through
  // a message.  Since we're not worried if we miss some of the measurements, just keep looping
  // until we have captured 50 bytes of data.  Then find the first occurrance of 0xAAAA (170 170
  // in decimal) which is the start of a message.  When/if we find that, read in the values
  if (cargoIdx>50) {
    #ifdef progDEBUG
      Serial.print("Read Serial1 "); Serial.print(cargoIdx-1); Serial.println(" bytes of incoming data");
    #endif
    boolean serial1Ready = false;
    int i=0;
    while (i<cargoIdx-1) {
      if (cargo[i] == 170 & cargo[i+1] == 170) {
        i+=3; // move past 0xAAAA and the 1-byte index number
        serial1Ready=true;
        break;
      }
      i++;
    }

    // if we've got the right starting point set up, read in the data
    if (serial1Ready) {
      Serial.print("  FOUND 0xAAAA! i ="); Serial.println(i);
      for (int j=i-3; j<i+16; j++) {
        Serial.print(cargo[j]); Serial.print(" ");
      }
      Serial.println(" ");
      yaw = (((int16_t)cargo[i+1] << 8) | cargo[i] ) / 100 ; 
      pitch = (((int16_t)cargo[i+3] << 8) | cargo[i+2] ) / 100;
      roll = (((int16_t)cargo[i+5] << 8) | cargo[i+4] ) / 100 ;
      cargoIdx=0;
      return true;
    }
    else {
      cargoIdx=0;
      return false;
    }
  }
  Serial.print("  cargoIdx ="); Serial.println(cargoIdx);
  return false;
}

/*
 * Get data from the Frame FSM300 Pan/Tilt/Roll board
 */
boolean getFrameOrient(){                                                               
  #ifdef progDEBUG
    if (Serial3.available()){
      Serial.println("Serial3 is ready ");
    }
    else {
      Serial.println("********** Serial3 is NOT ready ");
      return false;
    }
  #endif
  while (Serial3.available() && cargoIdx2<100){
    cargo2[cargoIdx2] = Serial3.read();
    cargoIdx2++;
  }
  
  // check if we've captured enough data.  A message from the board is 19 bytes long, but
  // there's no telling if we've captured it right at the beginning or if it's halfway through
  // a message.  Since we're not worried if we miss some of the measurements, just keep looping
  // until we have captured 50 bytes of data.  Then find the first occurrance of 0xAAAA (170 170
  // in decimal) which is the start of a message.  When/if we find that, read in the values
  if (cargoIdx2>50) {
    #ifdef progDEBUG
      Serial.print("Read Serial3 "); Serial.print(cargoIdx2-1); Serial.println(" bytes of incoming data");
    #endif
    boolean serial3Ready = false;
    int i=0;
    while (i<cargoIdx2-1) {
      if (cargo2[i] == 170 & cargo2[i+1] == 170) {
        i+=3; // move past 0xAAAA and the 1-byte index number
        serial3Ready=true;
        break;
      }
      i++;
    }

    // if we've got the right starting point set up, read in the data
    if (serial3Ready) {
      Serial.print("  FOUND 0xAAAA! i ="); Serial.println(i);
      for (int j=i-3; j<i+16; j++) {
        Serial.print(cargo2[j]); Serial.print(" ");
      }
      Serial.println(" ");
      yaw2 = (((int16_t)cargo2[i+1] << 8) | cargo2[i] ) / 100 ; 
      pitch2 = (((int16_t)cargo2[i+3] << 8) | cargo2[i+2] ) / 100;
      roll2 = (((int16_t)cargo2[i+5] << 8) | cargo2[i+4] ) / 100 ;
      cargoIdx2=0;
      return true;
    }
    else {
      cargoIdx2=0;
      return false;
    }
  }
  Serial.print("  cargoIdx2 ="); Serial.println(cargoIdx2);
  return false;
}

/**
 * Set ETEC preamplifier pins (gains or filters)
 * pinSet = int[] array containing Arduino pin numbers to set
 * bitmap = values to set
 * n = number of pins (must match size of pinSet
 */
void setEtecPins(int* pinSet, int bitmap, int n) {
  for (int i = 0; i < n; i++) {
    int aBit = ((bitmap & 1<<i) != 0) ? HIGH : LOW;
    #ifdef progDEBUG
      char str[60];
      sprintf(str, "Setting pin %d to %d\n", pinSet[i], aBit);
      Serial.println(str);
    #endif
    digitalWrite(pinSet[i], aBit);
  }
}

/*
 * ISR (interrupt) for detecting when actuator 1 status
 * changes - from stationary to moving, and vice-versa.
 */
/*
This ISR has been commented out - sending UDP messages within
an interrupt seems to cause the Arduino to freeze fairly regularly.
Have tried flushing the UDP channel and pausing after the UDP transmit,
but nothing seems to make the call more stable.  Instead of using an
interrupt, incorporate the status check into the main loop

void act1StatusCheck() {
  int stat = digitalRead(act1mov);
  char response[20];
  strcpy(response,"Actuator1 Stopped");    
  if (stat == LOW) {
    strcpy(response,"Actuator1 Moving");
  }
  #ifdef progDEBUG
    Serial.println(response);
  #endif

  Udp.flush();
  Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
  int retVal = Udp.write(response);
  Serial.print("UDP write error, retVal=");
  Serial.println(retVal);
  Udp.endPacket();  
  delay(10);
}
*/

/*
 * ISR (interrupt) for detecting when actuator 1 has a fault
 */
/*
This ISR has been commented out - sending UDP messages within
an interrupt seems to cause the Arduino to freeze fairly regularly.
Have tried flushing the UDP channel and pausing after the UDP transmit,
but nothing seems to make the call more stable.  Instead of using an
interrupt, incorporate the fault check into the main loop

void act1FaultDetected() {
  char response[]="**** Actuator1 Fault Detected ****";
  #ifdef progDEBUG
    Serial.println(response);
  #endif
  Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
  Udp.write(response);
  Udp.endPacket();  
}
*/
