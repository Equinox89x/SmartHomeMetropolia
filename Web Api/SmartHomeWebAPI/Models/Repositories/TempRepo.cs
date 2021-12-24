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
    public class TempRepo : ITempRepo
    {
        public async Task<ActionResult<Temperature>> GetTemperature(String temp)
        {
            try
            {
                string requestBody = temp;
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
