object Main extends App {

  // scalafix:off Option.get
  def fooBar(o: Option[Int]): Int =
    o.get
  // scalafix:o Option.get

}
