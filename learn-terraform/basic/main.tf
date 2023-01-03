resource "local_file" "my_file" {
  filename        = "tutorial_file.txt"
  content         = "Hello World! \nSubject from Future -> ${random_string.my_subject.result}\n"
  file_permission = "0765"
}

resource "random_string" "my_subject" {
  length           = 16
  special          = true
  override_special = "-_@^+'"
}

# How do you create 3 random strings in the same file?
resource "local_file" "my_file" {
  filename = "tutorial_file.txt"

  content         = <<-EOT
    "Hello World!"
    %{for result in random_string.my_string.*.result~}
    "Subject from Future -> ${result}"
    %{endfor~}
  EOT
  file_permission = "0765"
}

resource "random_string" "my_string" {
  count = 3

  length           = 16
  special          = true
  override_special = "-_@^+'"
}