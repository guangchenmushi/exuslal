$body = @{username='admin';password='123456'} | ConvertTo-Json
$login = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $body -ContentType 'application/json'
$token = $login.data.token
$h = @{Authorization="Bearer $token"}

Write-Host "=== Adding Classes ==="
$classes = @(
  ,@("计算机科学与技术2024-1班","CS2024-1","2024","计算机科学与技术")
  ,@("计算机科学与技术2024-2班","CS2024-2","2024","计算机科学与技术")
  ,@("软件工程2024-1班","SE2024-1","2024","软件工程")
  ,@("软件工程2024-2班","SE2024-2","2024","软件工程")
  ,@("数据科学2024-1班","DS2024-1","2024","数据科学与大数据技术")
  ,@("电子信息工程2023-1班","EE2023-1","2023","电子信息工程")
  ,@("数学与应用数学2023-1班","MATH2023-1","2023","数学与应用数学")
  ,@("英语2023-1班","ENG2023-1","2023","英语")
)
foreach ($c in $classes) {
  try {
    $json = @{name=$c[0]; code=$c[1]; grade=$c[2]; major=$c[3]} | ConvertTo-Json
    $r = Invoke-RestMethod -Uri 'http://localhost:8080/api/classes' -Method Post -Body $json -ContentType 'application/json' -Headers $h
    Write-Host "  OK: $($c[0])"
  } catch {
    Write-Host "  FAIL: $($c[0]) - $_"
  }
}

Write-Host "=== Adding Semesters ==="
$semesters = @(
  ,@("2024-2025学年第一学期","2024-1","2024-09-01","2025-01-15",$false)
  ,@("2024-2025学年第二学期","2024-2","2025-02-17","2025-07-04",$false)
  ,@("2025-2026学年第一学期","2025-1","2025-09-01","2026-01-16",$false)
  ,@("2025-2026学年第二学期","2025-2","2026-02-16","2026-07-03",$true)
)
foreach ($s in $semesters) {
  try {
    $json = @{name=$s[0]; code=$s[1]; startDate=$s[2]; endDate=$s[3]; isCurrent=$s[4]} | ConvertTo-Json
    $r = Invoke-RestMethod -Uri 'http://localhost:8080/api/semesters' -Method Post -Body $json -ContentType 'application/json' -Headers $h
    Write-Host "  OK: $($s[0])"
  } catch {
    Write-Host "  FAIL: $($s[0]) - $_"
  }
}

Write-Host "=== Done ==="
