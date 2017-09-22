def call(Closure body) {
  try {
    sh 'cico --help'
    withDuffyHost{
      body()
    }
  }
  catch (err) {
    echo "Only Duffy is currently supported."
    throw err
  }
}
