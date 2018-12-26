
Pod::Spec.new do |s|
  s.name         = "RNZenderPlayer"
  s.version      = "1.0.0"
  s.summary      = "RNZenderPlayer"
  s.description  = <<-DESC
                  RNZenderPlayer
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNZenderPlayer.git", :tag => "master" }
  s.source_files  = "RNZenderPlayer/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "Zender/Core"
  s.dependency "PhenixSdk"
  #s.dependency "others"

end

  
