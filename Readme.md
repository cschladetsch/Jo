# ![logo](res/logo.png) Jo - Git repo management

[![Build status](https://ci.appveyor.com/api/projects/status/github/cschladetsch/jo?svg=true)](https://ci.appveyor.com/project/cschladetsch/jo)
[![CodeFactor](https://www.codefactor.io/repository/github/cschladetsch/jo/badge)](https://www.codefactor.io/repository/github/cschladetsch/jo)

A tool to see the status of your git repos, and to switch between them quickly. Written in Java.

## Build
```bash
mvn clean install
```

## Install
Source `jo.sh` to add a bash function called `jo` that will invoke the `.jar` file, passing arguments, and processing the results.

## Usage

* `jo`  - list all got repos in `$WORK_DIR`
* `jo n` - move to nth repo, as shown by `jo` command
* `jo -` - go back to repo you came from

## Enter and Leave scripts

If a repo as a file called `.enter` anywhere from where you are leaving from, up to the root of the repo, then that script will be executed when you leave the repo via `jo`.

Similarly, if a repo as a file called `.leave` anywhere from where you are entering to, up to the root of the repo, then that script will be executed when you enter the repo via `jo`.

## Known Bugs
Lots. WIP.

