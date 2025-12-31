#!/usr/bin/env python3
"""
Script to fetch Mr. Robot TV series data from Wikipedia and save it to a text file.
"""

import wikipedia


def fetch_mr_robot_wiki():
    """Fetch Mr. Robot Wikipedia page and save content to a text file."""
    page_title = "Mr. Robot (TV series)"
    
    print(f"Fetching data about '{page_title}' from Wikipedia...")
    
    try:
        # Search for the page first to find exact title
        search_results = wikipedia.search("Mr. Robot TV series", results=5)
        print(f"Search results: {search_results}")
        
        # Try to fetch the page - use the TV series specific title
        page = wikipedia.page(page_title, auto_suggest=False)
        
        # Prepare content
        content = []
        content.append(f"Title: {page.title}")
        content.append(f"URL: {page.url}")
        content.append("=" * 80)
        content.append("")
        content.append("SUMMARY:")
        content.append(page.summary)
        content.append("")
        content.append("=" * 80)
        content.append("")
        content.append("FULL CONTENT:")
        content.append(page.content)
        
        # Save to file
        output_file = 'mr_robot_wiki.txt'
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(content))
        
        print(f"✓ Data successfully saved to {output_file}")
        print(f"  Title: {page.title}")
        print(f"  URL: {page.url}")
        print(f"  Content size: {len(page.content)} characters")
            
    except wikipedia.exceptions.DisambiguationError as e:
        print(f"✗ Multiple pages found. Options: {e.options[:5]}")
    except wikipedia.exceptions.PageError:
        print(f"✗ Page '{page_title}' not found on Wikipedia")
    except Exception as e:
        print(f"✗ An error occurred: {e}")


if __name__ == "__main__":
    fetch_mr_robot_wiki()
