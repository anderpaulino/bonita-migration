UPDATE theme SET content = CONVERT(VARBINARY(MAX), ?), cssContent = CONVERT(VARBINARY(MAX), ?) WHERE isDefault = 1 AND type = ?
