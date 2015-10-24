require 'open-uri'
require 'set'

game_ids = []
File.open("next_97_ids.txt") do |f|
  f.each do |line|
    game_ids << line.chomp
  end
end

File.open("next_usernames.txt", "w") do |user_file|
  File.open("processed_games.txt", "w") do |game_file|
    game_ids.each do |id|
      puts "Processing game: #{id}"
      begin
        rating_page_count = nil
        first_rating_page = open("http://boardgamegeek.com/collection/items/boardgame/#{id}?rated=1") do |f|
          f.each_line do |line|
            if line =~ /\/user\/([^\"\/]+)\"/
              puts "User: #{$1}"
              user_file.puts $1
            end
            if line =~ /\"last page">\[(\d+)\]/ then
              puts "got num: #{$1}"
              rating_page_count = $1.to_i
            end
          end
        end

        if rating_page_count
          (2..rating_page_count).each do |page_num|
            puts "\tPage: #{page_num}"
            next_page = open("http://boardgamegeek.com/collection/items/boardgame/#{id}/page/#{page_num}?rated=1") do |f|
              f.each_line do |line|
                if line =~ /\/user\/([^\"\/]+)\"/
                  puts "User: #{$1}"
                  user_file.puts $1
                end
              end
            end
          end
        end
        game_file.puts id
        game_file.flush
        user_file.flush
      rescue Error
        puts "Error processing: #{id}"
      rescue Exception
        puts "Error processing: #{id}"
      end
    end
  end
end
