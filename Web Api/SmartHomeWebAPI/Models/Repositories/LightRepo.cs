using Microsoft.AspNetCore.Mvc;
using Models.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using uPLibrary.Networking.M2Mqtt;

namespace Models
{
    public class LightRepo : ILightRepo
    {
        public async Task<ActionResult<KitchenLed>> KitchenLed(KitchenLed kitchenLed)
        {
            try
            {
                MqttClient client = new MqttClient("192.168.0.190");
                client.Connect(Guid.NewGuid().ToString());
                client.Publish("/PMS/kitchenled", Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(kitchenLed)));
                return new OkObjectResult(kitchenLed);
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        public async Task<ActionResult<BedroomLed>> BedroomLed(BedroomLed bedroomLed)
        {
            try
            {
                MqttClient client = new MqttClient("192.168.0.190");
                client.Connect(Guid.NewGuid().ToString());
                client.Publish("/PMS/bedroomled", Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(bedroomLed)));
                return new OkObjectResult(bedroomLed);
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        public async Task<ActionResult<GarageLed>> GarageLed(GarageLed garageLed)
        {
            try
            {
                MqttClient client = new MqttClient("192.168.0.190");
                client.Connect(Guid.NewGuid().ToString());
                client.Publish("/PMS/garageled", Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(garageLed)));
                return new OkObjectResult(garageLed);
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        public async Task<ActionResult<LivingroomLed>> LivingroomLed(LivingroomLed livingroomLed )
        {
            try
            {
                MqttClient client = new MqttClient("192.168.0.190");
                client.Connect(Guid.NewGuid().ToString());
                client.Publish("/PMS/livingroomled", Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(livingroomLed)));
                return new OkObjectResult(livingroomLed);
            }
            catch (Exception ex)
            {
                return null;
            }
        }

    }
}
