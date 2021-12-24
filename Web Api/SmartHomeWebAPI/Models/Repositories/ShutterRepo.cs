using Microsoft.AspNetCore.Mvc;
using Models.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using uPLibrary.Networking.M2Mqtt;

namespace Models.Repositories
{
    public class ShutterRepo : IShutterRepo
    {

        public async Task<ActionResult<Shutter1>> Shutter1(Shutter1 shutter)
        {
            try
            {
                MqttClient client = new MqttClient("192.168.0.190");
                client.Connect(Guid.NewGuid().ToString());
                client.Publish("/PMS/shutter1", Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(shutter)));
                return new OkObjectResult(shutter);
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        public async Task<ActionResult<Shutter2>> Shutter2(Shutter2 shutter)
        {
            try
            {
                MqttClient client = new MqttClient("192.168.0.190");
                client.Connect(Guid.NewGuid().ToString());
                client.Publish("/PMS/shutter2", Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(shutter)));
                return new OkObjectResult(shutter);
            }
            catch (Exception ex)
            {
                return null;
            }
        }

    }
}
