import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO
import threading
import glob
import time
from multiprocessing import Process
import requests
import json
from datetime import datetime

url = "http://192.168.0.140:54691/api/smarthome/temperature"
url2 = "http://192.168.0.140:54691/api/smarthome/alarm"
prevTemp = 9000
isTriggered = False

base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'
GPIO.setwarnings(False)

servo1 = 25
KitchenLed = 21
BedroomLed = 20
GarageLed = 19
LivingroomLed = 16
GPIO.setmode(GPIO.BCM)
GPIO.setup(KitchenLed, GPIO.OUT)
GPIO.output(KitchenLed, 0)
GPIO.setup(BedroomLed, GPIO.OUT)
GPIO.output(BedroomLed, 0)
GPIO.setup(GarageLed, GPIO.OUT)
GPIO.output(GarageLed, 0)
GPIO.setup(LivingroomLed, GPIO.OUT)
GPIO.output(LivingroomLed, 0)

GPIO.setup(6, GPIO.IN, pull_up_down=GPIO.PUD_UP)

GPIO.setup(servo1, GPIO.OUT)
# setting the PWM signal to 50 hertz
pwm = GPIO.PWM(servo1, 50)
#The cycle will be at zero on start
pwm.start(0)


#region Temperature
def read_temp_raw():
        f = open(device_file, 'r')
        lines = f.readlines()
        f.close()
        return lines
 
def read_temp():
    lines = read_temp_raw()
    while lines[0].strip()[-3:] != 'YES':
        time.sleep(0.2)
        lines = read_temp_raw()
    equals_pos = lines[1].find('t=')
    if equals_pos != -1:
        temp_string = lines[1][equals_pos+2:]
        temp_c = float(temp_string) / 1000.0
        return temp_c
    
def looper():
    while True:
        global prevTemp
        time.sleep(10)
        jsonobject= '{"temperature":'+ str(read_temp()) +'}'
        #print(read_temp())
        if(prevTemp == 9000):
            prevTemp = read_temp()
            #print(jsonobject)
            rest = requests.post(url, json=jsonobject)
            print('Sent jsob object: '+ rest.text + ', with status code: '+ str(rest.status_code))
            
        if(read_temp() >= prevTemp+1 or read_temp() <= prevTemp-1 ):
            rest = requests.post(url, json=jsonobject)
            print('Sent jsob object: '+ rest.text + ', with status code: '+ str(rest.status_code))
#endregion

#region MQTT
def on_connect(client, userdata, flags,rc):
    print("Connected with result code " + str(rc))
    client.subscribe("/PMS/kitchenled")
    client.subscribe("/PMS/bedroomled")
    client.subscribe("/PMS/garageled")
    client.subscribe("/PMS/livingroomled")
    client.subscribe("/PMS/shutter1")
    client.subscribe("/PMS/alarm")
    
def on_message(client, userdata, msg):
    
    txt = msg.payload.decode("utf-8")
    #print(msg.topic + " " + str(msg.payload))
    print(msg.payload)
    if(msg.payload== b'{"kitchenled":"on"}'):
        GPIO.output(KitchenLed, 1)
    if(msg.payload== b'{"kitchenled":"off"}'):
        GPIO.output(KitchenLed, 0)
        
    if(msg.payload== b'{"bedroomled":"on"}'):
        GPIO.output(BedroomLed, 1)
    if(msg.payload== b'{"bedroomled":"off"}'):
        GPIO.output(BedroomLed, 0)
        
    if(msg.payload== b'{"garageled":"on"}'):
        GPIO.output(GarageLed, 1)
    if(msg.payload== b'{"garageled":"off"}'):
        GPIO.output(GarageLed, 0)
        
    if(msg.payload== b'{"livingroomled":"on"}'):
        GPIO.output(LivingroomLed, 1)
    if(msg.payload== b'{"livingroomled":"off"}'):
        GPIO.output(LivingroomLed, 0)
        
    if(msg.payload == b'{"shutter1":"open"}'):
        SetAngle(180)
        
    if(msg.payload == b'{"shutter1":"close"}'):
        SetAngle(0)
        
def connection():
    client = mqtt.Client()
    client.on_connect  = on_connect
    client.on_message = on_message
    client.connect("192.168.0.190",1883,60)
    client.loop_forever()
#endregion

def SetAngle(angle):
    #the duty cycle is worked out based on the angle given
    duty = angle / 18 + 2
    GPIO.output(servo1, True)
    #change the duty cycle to match what was calculated
    #remember that to use the PWM part of Rpi.GPIO you need
    #to specify the speed/hertz it will turn at.
    pwm.ChangeDutyCycle(duty)
    time.sleep(1)
    GPIO.output(servo1, False)
    pwm.ChangeDutyCycle(0)

    
def sensorCallBack():
    while True:
        time.sleep(20)
        if(GPIO.input(6) == True):
            jsonobject= '{"alarm":"not triggered"}'
            rest = requests.post(url2, json=jsonobject)
            print('Sent jsob object: '+ rest.text + ', with status code: '+ str(rest.status_code))
        else:
            jsonobject= '{"alarm":"triggered"}'
            rest = requests.post(url2, json=jsonobject)
            print('Sent jsob object: '+ rest.text + ', with status code: '+ str(rest.status_code))
            
def test():
    while True:
        SetAngle(0)
        time.sleep(1)
        SetAngle(180)

if __name__ == '__main__':
    prevTemp = 9000
    
    Process(target=connection).start()
    Process(target=looper).start()
    Process(target=sensorCallBack).start()
    #connection()
    #test()
    
    

     
