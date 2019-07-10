# Natural Language Pre-processing Architecture

NLPA is a framework designed to operate in conjuction with BDP4J (https://github.com/sing-group/bdp4j) and able to extract texts from Twitter, Youtube Comments, text files, raw email files (.eml) or WARC (Web Archive) files. The extracted text can be preprocessed into a Dataset using task (org.bdp4j.pipe.Pipe) definitions. This framework incorporates more than 30 preprocessing tasks to transform the text. 

## Requirements

You need this user accounts:

* Babelnet / Babelfy
* Twitter
* Youtube

## Configuration files
To properly opearte with NLPA the following configuration files (and options) should be filled:

* `babelnet.var.properties`
    * `babelnet.dir` - Babelnet path. Directory where are babelnet files  .
    * `babelnet.key` - Babelnet key. If you log in babelnet website, this key is in RESTful information target.

* `babelfy.var.properties`
    * `babelfy.key` - The same key as Babelnet

* `configurations.ini`

```
[twitter] 
ConsumerKey=<YourConsumerKey> 
ConsumerSecret=<YourConsumerSecret>
AccessToken=<YourAccessToken>
AccessTokenSecret=<YourAccessTokenSecret>

[youtube]
APIKey=<YourYoutubeAPIKey>
```
We are aware that with youtube we are using unsecure API keys. Next versions will include more secure forms to access YouTube services (i.e. using OAuth schemes).

Please see files config/configurations.ini.example, config/babelnet.var.properties.example and config/babelfy.var.properties.example for further configuration details.

## Using NLPA

Todo

## License

Copyright (C) 2018 Sing Group (University of Vigo)

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
