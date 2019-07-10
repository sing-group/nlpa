# Natural Language Pre-processing Architecture

This file contains instructions about configuration files needed to run application.

You need this user accounts:

* Babelnet
* Twitter
* Youtube

## Configuration files

* `babelnet.var.properties`
    * `babelnet.dir` - Babelnet path. Directory where are babelnet files  .
    * `babelnet.key` - Babelnet key. If you log in babelnet website, this key is in RESTful information target.

* `babelfy.var.properties`
    * `babelfy.key` - The same key as Babelnet

* `configurations.ini`

```
[twitter]
ConsumerKey
ConsumerSecret
AccessToken
AccessTokenSecret

[youtube]
APIKey
```
## License

Copyright (C) 2018 Sing Group (University of Vigo)

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
