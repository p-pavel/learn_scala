/** рефлексивные экземпляры CanEqual для используемых типов
  */
object CanEqualInstances:
  import org.http4s
  given CanEqual[http4s.Method, http4s.Method] = CanEqual.derived
  given CanEqual[http4s.Uri.Path, http4s.Uri.Path] = CanEqual.derived
