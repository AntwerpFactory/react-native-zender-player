VERSION=0.0.1
PACKAGE_NAME=react-native-zender-player

package:
	@- mkdir dist
	@- rm dist/${PACKAGE_NAME}-${VERSION}.tgz
	cd .. ; tar -czv --exclude=${PACKAGE_NAME}/.git --exclude=*.tgz -f ${PACKAGE_NAME}/dist/${PACKAGE_NAME}-${VERSION}.tgz ${PACKAGE_NAME}/ 
