#!/bin/sh

# Copyright (c) 2015-present, Facebook, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='-Xmx2048m -Dfile.encoding=UTF-8'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn () {
    echo >&2 "$*"
}

die () {
    echo >&2 "$*"
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
esac

# Attempt to set APP_HOME

# Resolve links: $0 may be a link
app_path=$0

# Need this for daisy-chained symlinks.
while
    APP_HOME=`dirname "$app_path"`
    fname=`basename "$app_path"`
    [ -h "$APP_HOME/$fname" ]
do
    l=`ls -ld "$APP_HOME/$fname"`
    case $l in
      *\"*\"*)
        # BSD style
        link=`echo $l | sed -E 's/.*"(.*)".*/\1/g'`
        ;;
      *)
        # GNU style
        link=`echo $l | sed -e 's/.*-> //'`
        ;;
    esac

    case $link in
      /*)
        app_path=$link
        ;;
      *)
        app_path=$APP_HOME/$link
        ;;
    esac
done

# Set search paths
APP_HOME_DIR=`dirname "$app_path"`
APP_BASE_NAME=`basename "$0"`

# If APP_HOME_DIR is '.', we need to resolve to the current directory
if [ "$APP_HOME_DIR" = "." ]; then
    APP_HOME_DIR=`pwd`
else
    APP_HOME_DIR=`cd "$APP_HOME_DIR" && pwd`
fi
APP_HOME="$APP_HOME_DIR"

# For Cygwin or MSYS, switch paths to Windows format before running java
if $cygwin || $msys ; then
  APP_HOME="`cygpath --path --mixed "$APP_HOME"`"
  APP_BASE_NAME="`cygpath --windows "$APP_BASE_NAME"`"
  APP_HOME_DIR="`cygpath --path --mixed "$APP_HOME_DIR"`"
fi

# For POSIX shells, we need to handle JVM_OPTS differently since arrays aren't supported
# Combine all JVM options into a single string
JVM_OPTS="$DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS -Dorg.gradle.appname=$APP_BASE_NAME"

# Find java from JAVA_HOME
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if ! "$cygwin" && ! "$darwin" ; then
    case $MAX_FD in
      max*)
        # In POSIX sh, ulimit -H is undefined. That's why the result is checked to see if it's valid
        # In most shells, at least one of these alternatives work
        NEW_MAX_FD=`ulimit -H -n` ||
        NEW_MAX_FD=`ulimit -n` ||
        warn "Could not query maximum file descriptor limit"
    if [ $NEW_MAX_FD = "unlimited" ] ; then
        NEW_MAX_FD=65536
    fi
        ;;
      *)
        NEW_MAX_FD=$MAX_FD
        ;;
    esac
    if ! "$cygwin" ; then
        ulimit -n $NEW_MAX_FD ||
        warn "Could not set maximum file descriptor limit: $NEW_MAX_FD"
    fi
fi

# Collect all arguments for the java command:
#   * Based on classpath
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
"$JAVACMD" $JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"