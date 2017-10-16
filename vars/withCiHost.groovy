def call(Closure body) {
  def provisioner=null
  try {
    sh 'cico --help > /dev/null'
    provisioner='duffy'
  }
  catch (err) {
    echo "Only Duffy is currently supported."
    throw err
  }

  if (provisioner == 'duffy') {
    withDuffyHost{
      body()
    }
  }
}
