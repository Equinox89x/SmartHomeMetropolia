using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Models.Models;

namespace Models
{
    public interface ILightRepo
    {
        Task<ActionResult<BedroomLed>> BedroomLed(BedroomLed bedroomLed);
        Task<ActionResult<GarageLed>> GarageLed(GarageLed garageLed);
        Task<ActionResult<KitchenLed>> KitchenLed(KitchenLed kitchenLed);
        Task<ActionResult<LivingroomLed>> LivingroomLed(LivingroomLed livingroomLed);
    }
}