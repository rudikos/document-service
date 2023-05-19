package com.workwolf.documentservice.service;

import org.json.simple.JSONObject;

public interface DocumentInspectService {
    JSONObject inspect(String fileName);
}
