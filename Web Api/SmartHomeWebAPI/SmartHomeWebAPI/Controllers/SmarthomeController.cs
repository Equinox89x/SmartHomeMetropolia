using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Models;
using Models.Models;
using Models.Repositories;

namespace SmartHomeWebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SmarthomeController : ControllerBase
    {

        private readonly ILightRepo _lightRepo;
        private readonly ITempRepo _tempRepo;
        private readonly IShutterRepo _shutterRepo;
        private readonly IAlarmRepo _alarmRepo;
        public SmarthomeController(ILightRepo lightRepo, ITempRepo tempRepo, IShutterRepo shutterRepo, IAlarmRepo alarmRepo)
        {
            _lightRepo = lightRepo;
            _tempRepo = tempRepo;
            _shutterRepo = shutterRepo;
            _alarmRepo = alarmRepo;
        }

        #region LED's
        [HttpPost]
        [Route("kitchenled")]
        public async Task<ActionResult<KitchenLed>> KitchenLed(KitchenLed kitchenLed)
        {
            try
            {
                if(kitchenLed != null)
                {
                    await _lightRepo.KitchenLed(kitchenLed);
                    return kitchenLed;
                }
                else
                {
                    return StatusCode(404);
                }
            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }

        [HttpPost]
        [Route("bedroomled")]
        public async Task<ActionResult<BedroomLed>> BedroomLed(BedroomLed bedroomLed)
        {
            try
            {
                if (bedroomLed != null)
                {
                    await _lightRepo.BedroomLed(bedroomLed);
                    return bedroomLed;
                }
                else
                {
                    return StatusCode(404);
                }

            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }

        [HttpPost]
        [Route("livingroomled")]
        public async Task<ActionResult<LivingroomLed>> LivingroomLed(LivingroomLed livingroomLed)
        {
            try
            {
                if (livingroomLed != null)
                {
                    await _lightRepo.LivingroomLed(livingroomLed);
                    return livingroomLed;
                }
                else
                {
                    return StatusCode(404);
                }

            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }

        [HttpPost]
        [Route("garageled")]
        public async Task<ActionResult<GarageLed>> GarageLed(GarageLed garageLed)
        {
            try
            {
                if (garageLed != null)
                {
                    await _lightRepo.GarageLed(garageLed);
                    return garageLed;
                }
                else
                {
                    return StatusCode(404);
                }

            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }
        #endregion

        #region Temperature
        private static string Temp = "–273.15";

        public static string _temp
        {
            get { return _temp; }
            set { _temp = value; }
        }

        [HttpPost]
        [Route("temperature")]
        public ActionResult PostTemperature(object temperature)
        {
            try
            {
                if(temperature != null)
                {

                    Temp = temperature.ToString();
                    return new OkObjectResult(Temp);
                }
                else
                {
                    return StatusCode(404);
                }

            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }

        [HttpGet]
        [Route("temperature")]
        public async Task<ActionResult<Temperature>> GetTemperature()
        {
            try
            {
                if(Temp != null)
                {
                    var temp = Temp;
                    return await _tempRepo.GetTemperature(temp);
                }
                else
                {
                    return StatusCode(404);
                }

            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }
        #endregion

        #region Alarm
        private static string AlarmStatus = "not triggered";

        public static string _alarmStatus
        {
            get { return _alarmStatus; }
            set { _alarmStatus = value; }
        }

        [HttpPost]
        [Route("alarm")]
        public ActionResult PostAlarm(object alarm)
        {
            try
            {
                if (alarm != null)
                {

                    AlarmStatus = alarm.ToString();
                    return new OkObjectResult(AlarmStatus);
                }
                else
                {
                    return StatusCode(404);
                }

            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }

        [HttpGet]
        [Route("alarm")]
        public async Task<ActionResult<Alarm>> GetAlarm()
        {
            try
            {
                if (AlarmStatus != null)
                {
                    var alarm = AlarmStatus;
                    return await _alarmRepo.GetAlarm(alarm);
                }
                else
                {
                    return StatusCode(404);
                }

            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }
        #endregion

        #region Shutters
        [HttpPost]
        [Route("shutter1")]
        public async Task<ActionResult<Shutter1>> Shutter1(Shutter1 shutter)
        {
            try
            {
                if (shutter != null)
                {
                    await _shutterRepo.Shutter1(shutter);
                    return shutter;
                }
                else
                {
                    return StatusCode(404);
                }
            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }

        [HttpPost]
        [Route("shutter2")]
        public async Task<ActionResult<Shutter2>> Shutter2(Shutter2 shutter)
        {
            try
            {
                if (shutter != null)
                {
                    await _shutterRepo.Shutter2(shutter);
                    return shutter;
                }
                else
                {
                    return StatusCode(404);
                }
            }
            catch (Exception ex)
            {
                return StatusCode(500);
            }
        }
        #endregion
    }
}