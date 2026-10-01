export function getUTF8ByteLength(str) {
  // 创建 TextEncoder 实例
  const encoder = new TextEncoder()
  // 将字符串编码为 Uint8Array（字节数组）
  const parsed = encoder.encode(str)
  console.log(parsed.length);
  // 返回字节数组的长度
  return parsed.length
}