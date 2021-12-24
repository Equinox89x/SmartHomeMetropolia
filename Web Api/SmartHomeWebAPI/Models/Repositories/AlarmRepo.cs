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
    public class AlarmRepo : IAlarmRepo
    {
        public async Task<ActionResult<Alarm>> GetAlarm(string alarm)
        {
            try
            {
                string requestBody = alarm;
                var reg = JsonConvert.DeserializeObject(requestBody);
                return new OkObjectResult(reg);
            }
            catch (Exception ex)
            {
                return null;
            }
        }
    }
}
