package io.tronbot.dc.config
/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface Constants {

	// Spring profiles for development, test and production, see http://jhipster.github.io/profiles/
	public static final String SPRING_PROFILE_DEVELOPMENT = 'dev'
	public static final String SPRING_PROFILE_TEST = 'test'
	public static final String SPRING_PROFILE_PRODUCTION = 'prod'
	// Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
	public static final String SPRING_PROFILE_CLOUD = 'cloud'
	// Spring profile used to disable swagger
	public static final String SPRING_PROFILE_SWAGGER = 'swagger'

}
