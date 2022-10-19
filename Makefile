# Makefile project ot compile and run Server and Client
# PHONY Targets: all, clean, run_server, run_client, jar_server, jar_client
# Private Targets: compile_server, compile_client, compile_common
# Additional libraries needed by the Server are located in the lib folder

# Variables
PACKAGE_PATH = src
BUILD_PATH = build
SERVER_MAIN_CLASS = ServerMain
CLIENT_MAIN_CLASS = ClientMain
SERVER_PACKAGES = winsome_comunication winsome_DB winsome_server
CLIENT_PACKAGES = winsome_comunication winsome_client
SERVER_LIBS = lib/*

compile_server:
	echo "Compiling Server"

compile_client:
	echo "Compiling Client"
