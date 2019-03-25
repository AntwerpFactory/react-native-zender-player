.PHONY: dist
VERSION=1.0.0
PACKAGE_NAME=react-native-zender-player

dist:
	@- mkdir dist
	@- rm dist/${PACKAGE_NAME}-${VERSION}.tgz
	cd .. ; tar -czv --exclude=${PACKAGE_NAME}/.git --exclude=react-native-zender-player/android/build/ --exclude=*.tgz -f ${PACKAGE_NAME}/dist/${PACKAGE_NAME}-${VERSION}.tgz ${PACKAGE_NAME}/ 

pack:
	npm pack
