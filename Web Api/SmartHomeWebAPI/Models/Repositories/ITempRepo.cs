using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Models.Models;

namespace Models.Repositories
{
    public interface ITempRepo
    {
        Task<ActionResult<Temperature>> GetTemperature(String temp);
    }
}