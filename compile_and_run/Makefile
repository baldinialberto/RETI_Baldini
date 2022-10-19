# Makefile project ot compile and run Server and Client
# PHONY Targets: all, clean, run_server, run_client, jar_server, jar_client
# Additional libraries needed by the Server are located in the lib folder

# Variables
BUILD_PATH = ../bin

PHONY : all clean run_client run_server

all : jar_client jar_server

classes:
	javac -d $(BUILD_PATH) -cp @compiler_classpaths @compiler_classes

clean:
	rm -rf $(BUILD_PATH)

run_server:
	java -cp @classpaths_server winsome_server.ServerMain &> server.log &

run_client:
	java -cp @classpaths_client winsome_client.ClientMain

jar_server: classes
	jar -cvf $(BUILD_PATH)/Server.jar @jar_server

jar_client: classes
	jar -cvf $(BUILD_PATH)/Client.jar @jar_client