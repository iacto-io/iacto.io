package io.iacto

case class Schema(columns: List[Column]) {
  def headers: List[Header] = columns.map(_.header)
  def apply(row: Map[Header, String]): Map[Header, String] =
    columns.map(c => c.header -> c.formula(row)).toMap
}
