package com.example.tinyurl.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置類別
 *
 * 配置 Redis 連線池、序列化器與 RedisTemplate
 * 支援 JSON 序列化與連線池管理
 */
@Configuration
@EnableCaching
public class RedisConfig {

  @Value("${spring.data.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.data.redis.port:6379}")
  private int redisPort;

  @Value("${spring.data.redis.password:}")
  private String redisPassword;

  /**
   * 配置 Redis 連線工廠
   *
   * @return JedisConnectionFactory Redis 連線工廠
   */
  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(redisHost);
    config.setPort(redisPort);

    if (redisPassword != null && !redisPassword.trim().isEmpty()) {
      config.setPassword(redisPassword);
    }

    JedisConnectionFactory factory = new JedisConnectionFactory(config);

    System.out.println("Redis 連線配置: host=" + redisHost + ", port=" + redisPort);

    return factory;
  }

  /**
   * 配置快取專用的 ObjectMapper
   *
   * @return ObjectMapper JSON 序列化器
   */
  @Bean("cacheObjectMapper")
  public ObjectMapper cacheObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.findAndRegisterModules();
    return mapper;
  }

  /**
   * 配置 RedisTemplate
   *
   * @param connectionFactory Redis 連線工廠
   * @return RedisTemplate Redis 操作模板
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // 字串序列化器
    StringRedisSerializer stringSerializer = new StringRedisSerializer();

    // JSON 序列化器 (使用通用的 Jackson 序列化器)
    GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(cacheObjectMapper());

    // 設定序列化器
    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.afterPropertiesSet();

    System.out.println("RedisTemplate 配置完成，使用 JSON 序列化");

    return template;
  }
}
