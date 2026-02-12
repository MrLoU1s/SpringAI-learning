package com.muiyurocodes.learning_spring_ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TravellingTools {

    @Tool(description = "Get the weather for the city. ")
    public String getWeather(@ToolParam(description = "City from which to retrieve the weather information.") String city){
        return switch (city){
            case "Delhi"-> "Sunny, 26 Degrees";
            case "London" -> "Cloudy, 5 Degrees";
            default -> "Cannot identify city";
        };
    }
}
