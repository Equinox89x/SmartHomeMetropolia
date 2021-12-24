using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Models.Models
{
    public class KitchenLed
    {

        [DataType(DataType.Text)]
        public string kitchenled { get; set; }

    }

    public class BedroomLed
    {

        [DataType(DataType.Text)]
        public string bedroomled { get; set; }

    }

    public class GarageLed
    {

        [DataType(DataType.Text)]
        public string garageled { get; set; }

    }

    public class LivingroomLed
    {

        [DataType(DataType.Text)]
        public string livingroomled { get; set; }

    }

    public class Temperature
    {

        public float temperature { get; set; }

    }

    public class Humidity
    {

        public float humidity { get; set; }

    }

    public class Shutter1
    {

        public string shutter1 { get; set; }

    }

    public class Shutter2
    {

        public string shutter2 { get; set; }

    }

    public class Alarm
    {
        public string alarm { get; set; }
    }
}
