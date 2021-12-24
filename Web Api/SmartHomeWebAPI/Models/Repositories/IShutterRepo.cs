using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Models.Models;

namespace Models.Repositories
{
    public interface IShutterRepo
    {
        Task<ActionResult<Shutter1>> Shutter1(Shutter1 shutter);
        Task<ActionResult<Shutter2>> Shutter2(Shutter2 shutter);
    }
}