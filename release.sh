#!/bin/sh
echo ""
echo "##############################################"
echo "  Releasing Design Time Governance (Community)"
echo "##############################################"
echo ""
mvn -e --batch-mode clean release:prepare release:perform

