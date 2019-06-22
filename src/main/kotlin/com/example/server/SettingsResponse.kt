package com.example.server

data class SettingsResponse(var store:String,
                            var searchUrl:String,
                            var pathToBlock:String,
                            var relPathToName:String,
                            var relPathToImg:String,
                            var relPathToPrice:String,
                            var relPathToAuthor:String,
                            var relPathToBook:String,
                            var pathToISBN:String,
                            var delimiter:String)
{
    constructor():this("","","","",
            "","","",
            "","","")
}