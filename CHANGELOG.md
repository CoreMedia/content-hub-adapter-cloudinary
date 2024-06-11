# Changelog

## 2.0.4

- Use Java 17 SDK
- Use Node 18.x
- Compatible CoreMedia Content Cloud version: `2401.3`

## 2.0.3
* Improved browse mode with pagination support
* Rudimentary support for search (on filename)
* New configuration options
  * Options for using asset_id as externalRefId (experimental)  
    * assetIdModeEnabled (String: true/false, default false)
    * importPublicIdAs (String: name of property, default null)
  * Option to configure search query expression
    * searchQuery(String: query expression, default filename=%1$s)   
  * Options to control video import behaviour
    * importVideoBlob (String: true/false, default true)
    * importVideoURL (String: true/false, default false)
  * Options to control image import behaviour 
    * importImageMaxWidth (Integer, default null)
    * importImageQuality (Integer, 1-100, default null)
* Experimental support for using asset_id as externalRefId, see above
* Other enhancements and bugfixes 
  * Thumbnail preview for Videos
  * Display asset size in preview 
  * Correct Breadcrumb in Studio Library
* Updated to use Cloudinary API version 1.37.0 

## 2.0.0
* Initial release of the plugin for CMCC 11.2110.1 including all features known from latest version of CMCCv10.

## 1.0.2
* Compatibility for CoreMedia Content Cloud release 2107.1
* Added Mime-Type and Content Type mappings
* Added support for thumbnail preview in Studio details panel
* Bugfixes

## 1.0.1
* Compatibility for CoreMedia Content Cloud release 2101.1
* Bugfixes

## 1.0.0
* Switch from Extensions to Plugin architecture, suitable for CMCC 2101 and newer.




<!--
### General Notes 

* Update 


2007
--------------------------------------------------------------------------------

### Switch from Extensions to Plugin architecture

Suitable for CMCC 2101 and newer. 


2010.1-1
--------------------------------------------------------------------------------

### Initial Release 🥳

Basic implementation of the Content Hub API.
