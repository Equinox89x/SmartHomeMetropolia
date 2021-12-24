using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Models.Models;

namespace Models.Repositories
{
    public interface IAlarmRepo
    {
        Task<ActionResult<Alarm>> GetAlarm(string alarm);
    }
}