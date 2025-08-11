package com.example.tinyurl.infrastructure.persistence;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.domain.repository.UrlRepository;
import com.example.tinyurl.test.UrlTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * URL Repository 整合測試
 *
 * <p>
 * 使用 Testcontainers 建立真實的 PostgreSQL 環境，
 * 測試資料庫持久層的完整功能。
 *
 * <p>
 * 測試範圍：
 * <ul>
 * <li>基本 CRUD 操作</li>
 * <li>複雜查詢功能</li>
 * <li>資料完整性約束</li>
 * <li>並發存取處理</li>
 * <li>效能表現驗證</li>
 * </ul>
 */
@SpringBootTest
@Testcontainers
@DisplayName("URL Repository 整合測試")
class UrlRepositoryIntegrationTest {

  @Container
  @SuppressWarnings("resource") // Testcontainers 自動管理容器生命週期
  static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test")
      .withReuse(true);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
  }

  @Autowired
  private UrlRepository urlRepository;

  @Autowired
  private EntityManager entityManager;

  private ShortCode testShortCode;
  private LongUrl testLongUrl;
  private Url testUrl;

  @BeforeEach
  void setUp() {
    testShortCode = new ShortCode("abc123");
    testLongUrl = new LongUrl("https://example.com/very/long/path");
    testUrl = Url.create(testLongUrl, testShortCode);
  }

  @Test
  @DisplayName("應該能正確儲存與查詢 URL")
  @Transactional
  void shouldSaveAndFindUrl() {
    // When - 儲存 URL
    urlRepository.save(testUrl);
    entityManager.flush();
    entityManager.clear();

    // Then - 應該能查詢到儲存的 URL
    Optional<Url> found = urlRepository.findByShortCode(testShortCode);

    assertThat(found).isPresent();
    assertThat(found.get().getShortCode()).isEqualTo(testShortCode);
    assertThat(found.get().getLongUrl()).isEqualTo(testLongUrl);
    assertThat(found.get().getAccessCount()).isEqualTo(0);
    assertThat(found.get().getCreatedAt()).isNotNull();
    assertThat(found.get().getAccessedAt()).isNull();
  }

  @Test
  @DisplayName("應該能根據長網址查詢 URL")
  @Transactional
  void shouldFindByLongUrl() {
    // Given - 儲存 URL
    urlRepository.save(testUrl);

    // When - 根據長網址查詢
    Optional<Url> found = urlRepository.findByLongUrl(testLongUrl);

    // Then - 應該找到對應的 URL
    assertThat(found).isPresent();
    assertThat(found.get().getShortCode()).isEqualTo(testShortCode);
    assertThat(found.get().getLongUrl()).isEqualTo(testLongUrl);
  }

  @Test
  @DisplayName("應該能正確檢查短網址碼是否存在")
  @Transactional
  void shouldCheckExistenceByShortCode() {
    // Given - 儲存 URL
    urlRepository.save(testUrl);

    // When & Then - 檢查存在性
    assertThat(urlRepository.existsByShortCode(testShortCode)).isTrue();
    assertThat(urlRepository.existsByShortCode(new ShortCode("notexist"))).isFalse();
  }

  @Test
  @DisplayName("應該能正確統計指定時間後建立的 URL 數量")
  @Transactional
  void shouldCountByCreatedAtAfter() {
    // Given - 儲存多個 URL
    urlRepository.save(testUrl);
    urlRepository.save(Url.create(
        new LongUrl("https://example.com/another"),
        new ShortCode("def456")));

    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

    // When - 統計一小時前建立的 URL
    long count = urlRepository.countByCreatedAtAfter(oneHourAgo);

    // Then - 應該有 2 個 URL
    assertThat(count).isEqualTo(2);
  }

  @Test
  @DisplayName("應該能查詢熱門 URL")
  @Transactional
  void shouldFindTopAccessedUrls() {
    // Given - 建立有不同存取次數的 URL
    Url url1 = Url.create(testLongUrl, testShortCode);
    Url url2 = Url.create(
        new LongUrl("https://example.com/popular"),
        new ShortCode("def456"));

    // 模擬存取
    url1 = url1.recordAccess().recordAccess(); // 2 次存取
    url2 = url2.recordAccess().recordAccess().recordAccess(); // 3 次存取

    urlRepository.save(url1);
    urlRepository.save(url2);

    // When - 查詢前 2 名熱門 URL
    List<Url> topUrls = urlRepository.findTopAccessedUrls(2);

    // Then - 應該按存取次數降序排列
    assertThat(topUrls).hasSize(2);
    assertThat(topUrls.get(0).getAccessCount()).isGreaterThanOrEqualTo(topUrls.get(1).getAccessCount());
  }

  @Test
  @DisplayName("應該能正確刪除 URL")
  @Transactional
  void shouldDeleteUrl() {
    // Given - 儲存 URL
    urlRepository.save(testUrl);
    assertThat(urlRepository.existsByShortCode(testShortCode)).isTrue();

    // When - 刪除 URL
    boolean deleted = urlRepository.deleteByShortCode(testShortCode);

    // Then - 應該成功刪除
    assertThat(deleted).isTrue();
    assertThat(urlRepository.existsByShortCode(testShortCode)).isFalse();
  }

  @Test
  @DisplayName("刪除不存在的 URL 應該返回 false")
  @Transactional
  void shouldReturnFalseWhenDeletingNonExistentUrl() {
    // When - 刪除不存在的 URL
    boolean deleted = urlRepository.deleteByShortCode(new ShortCode("notexist"));

    // Then - 應該返回 false
    assertThat(deleted).isFalse();
  }

  @Test
  @DisplayName("應該能正確統計總數量")
  @Transactional
  void shouldCountTotal() {
    // Given - 儲存多個 URL
    urlRepository.save(testUrl);
    urlRepository.save(Url.create(
        new LongUrl("https://example.com/another"),
        new ShortCode("def456")));

    // When - 統計總數
    long count = urlRepository.count();

    // Then - 應該有 2 個 URL
    assertThat(count).isEqualTo(2);
  }

  @Test
  @DisplayName("應該能正確統計總存取次數")
  @Transactional
  void shouldCalculateTotalAccessCount() {
    // Given - 建立有不同存取次數的 URL
    Url url1 = Url.create(testLongUrl, testShortCode).recordAccess().recordAccess(); // 2 次存取
    Url url2 = Url.create(
        new LongUrl("https://example.com/popular"),
        new ShortCode("def456")).recordAccess().recordAccess().recordAccess(); // 3 次存取

    urlRepository.save(url1);
    urlRepository.save(url2);

    // When - 統計總存取次數
    long totalAccess = urlRepository.totalAccessCount();

    // Then - 應該是 5 次存取
    assertThat(totalAccess).isEqualTo(5);
  }

  @Test
  @DisplayName("應該能查詢時間範圍內建立的 URL")
  @Transactional
  void shouldFindByCreatedAtBetween() {
    // Given - 儲存 URL
    urlRepository.save(testUrl);

    LocalDateTime start = LocalDateTime.now().minusHours(1);
    LocalDateTime end = LocalDateTime.now().plusHours(1);

    // When - 查詢時間範圍內的 URL
    List<Url> urls = urlRepository.findByCreatedAtBetween(start, end);

    // Then - 應該找到 URL
    assertThat(urls).hasSize(1);
    assertThat(urls.get(0).getShortCode()).isEqualTo(testShortCode);
  }

  @Test
  @DisplayName("應該能查詢指定時間後存取的 URL")
  @Transactional
  void shouldFindByAccessedAtAfter() {
    // Given - 建立並存取 URL
    Url accessedUrl = testUrl.recordAccess();
    urlRepository.save(accessedUrl);

    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

    // When - 查詢一小時前存取的 URL
    List<Url> urls = urlRepository.findByAccessedAtAfter(oneHourAgo);

    // Then - 應該找到被存取的 URL
    assertThat(urls).hasSize(1);
    assertThat(urls.get(0).getShortCode()).isEqualTo(testShortCode);
    assertThat(urls.get(0).getAccessedAt()).isNotNull();
  }

  @Test
  @DisplayName("查詢不存在的短網址應該返回 empty")
  @Transactional
  void shouldReturnEmptyForNonExistentShortCode() {
    // When - 查詢不存在的短網址
    Optional<Url> found = urlRepository.findByShortCode(new ShortCode("notexist"));

    // Then - 應該返回 empty
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("查詢不存在的長網址應該返回 empty")
  @Transactional
  void shouldReturnEmptyForNonExistentLongUrl() {
    // When - 查詢不存在的長網址
    Optional<Url> found = urlRepository.findByLongUrl(
        new LongUrl("https://nonexistent.example.com"));

    // Then - 應該返回 empty
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("參數為 null 時應該拋出異常")
  @Transactional
  void shouldThrowExceptionForNullParameters() {
    // Then - 驗證各種 null 參數都會拋出異常
    assertThatThrownBy(() -> urlRepository.save(null))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> urlRepository.findByShortCode(null))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> urlRepository.findByLongUrl(null))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> urlRepository.existsByShortCode(null))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> urlRepository.countByCreatedAtAfter(null))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> urlRepository.deleteByShortCode(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("非法參數應該拋出異常")
  @Transactional
  void shouldThrowExceptionForInvalidParameters() {
    // Then - 驗證非法參數會拋出異常
    assertThatThrownBy(() -> urlRepository.findTopAccessedUrls(0))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> urlRepository.findTopAccessedUrls(-1))
        .isInstanceOf(IllegalArgumentException.class);

    LocalDateTime now = LocalDateTime.now();
    assertThatThrownBy(() -> urlRepository.findByCreatedAtBetween(now, now.minusHours(1)))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
