package com.example.tinyurl.adapters.web.mapper;

import com.example.tinyurl.adapters.web.dto.CreateUrlRequest;
import com.example.tinyurl.adapters.web.dto.CreateUrlResponse;
import com.example.tinyurl.adapters.web.dto.UrlInfoResponse;
import com.example.tinyurl.application.dto.CreateShortUrlResponse;
import com.example.tinyurl.application.dto.GetLongUrlResponse;
import com.example.tinyurl.application.port.in.CreateShortUrlCommand;
import com.example.tinyurl.application.port.in.GetLongUrlCommand;
import com.example.tinyurl.application.port.in.RedirectUrlCommand;
import org.springframework.stereotype.Component;

/**
 * Web 層資料轉換器
 *
 * <p>
 * 負責 Web DTO 與應用層 DTO/Command 之間的轉換。
 * 分離 Web 介面關注點與應用層業務邏輯。
 */
@Component
public class UrlWebMapper {

  /**
   * 將 Web 請求轉換為應用層指令
   *
   * @param request Web 請求 DTO
   * @return 建立短網址指令
   */
  public CreateShortUrlCommand toCommand(CreateUrlRequest request) {
    return new CreateShortUrlCommand(
        request.longUrl(),
        request.getTtl());
  }

  /**
   * 將應用層回應轉換為 Web 回應
   *
   * @param useCaseResponse 應用層回應
   * @return Web 回應 DTO
   */
  public CreateUrlResponse toResponse(CreateShortUrlResponse useCaseResponse) {
    return new CreateUrlResponse(
        useCaseResponse.shortCode(),
        useCaseResponse.longUrl(),
        useCaseResponse.shortUrl(),
        useCaseResponse.createdAt(),
        useCaseResponse.ttl() != null ? useCaseResponse.ttl().getSeconds() : null);
  }

  /**
   * 將短網址碼轉換為查詢指令
   *
   * @param shortCode 短網址碼
   * @return 查詢長網址指令
   */
  public GetLongUrlCommand toGetLongUrlCommand(String shortCode) {
    return new GetLongUrlCommand(shortCode);
  }

  /**
   * 將短網址碼轉換為重定向指令
   *
   * @param shortCode 短網址碼
   * @return 重定向指令
   */
  public RedirectUrlCommand toRedirectCommand(String shortCode) {
    return new RedirectUrlCommand(shortCode);
  }

  /**
   * 將應用層回應轉換為 URL 資訊回應
   *
   * @param useCaseResponse 應用層回應
   * @return URL 資訊回應 DTO
   */
  public UrlInfoResponse toUrlInfoResponse(GetLongUrlResponse useCaseResponse) {
    return new UrlInfoResponse(
        useCaseResponse.shortCode(),
        useCaseResponse.longUrl(),
        useCaseResponse.createdAt(),
        useCaseResponse.accessedAt(),
        useCaseResponse.accessCount());
  }
}
