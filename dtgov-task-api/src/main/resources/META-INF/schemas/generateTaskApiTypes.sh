#!/bin/sh

xjc task-api.xsd -b jaxb-bindings.xml -d ../../../java -p org.overlord.dtgov.taskapi.types
