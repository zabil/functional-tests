[![Gauge
Badge](https://cdn.rawgit.com/getgauge/getgauge.github.io/master/Gauge_Badge.svg)](http://getgauge.io)

## Pre-Requisites
* Java
* Gradle
* Maven
* [Node](https://nodejs.org/en/) 
* [Gauge](http://getgauge.io)
* Mercurial
* Git

## Setup
* ``` git clone``` as a sibling directory to
  [go.cd](https://github.com/gocd/gocd)
* ```$ cd gocd-functional-tests```
* ```$ gauge --install-all```

## Prepare
```
$ rake prepare
```
## Running tests

```
$ GO_VERSION=X.x.x gauge specs/AdminTaskListing.spec 
```

## Contributing

We encourage you to contribute to Go. For information on contributing to this project, please see our [contributor's guide](http://www.go.cd/contribute).
A lot of useful information like links to user documentation, design documentation, mailing lists etc. can be found in the [resources](http://www.go.cd/community/resources.html) section.


## License

```plain
Copyright 2016 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
